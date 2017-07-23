package io.mycat.jredis.util;

import io.mycat.jredis.constant.RedisConstant;
import io.mycat.jredis.datastruct.*;
import io.mycat.jredis.memory.MemoryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * Desc:支持插入、删除、替换、查找和获取随机元素等操作
 *
 * @date: 21/07/2017
 * @author: gaozhiwen
 */
public class DictUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(DictUtil.class);

    /**
     * 创建一个新的字典
     *
     * @param type
     * @param privData
     * @return
     */
    public static Dict dictCreate(DictType type, Object privData) {
        Dict dict = new Dict();
        int size = MemoryManager.sizeOf(dict);
        long dictAdd = MemoryManager.alloc(size);

        if (dictAdd == 0)
            return null;

        dict.setAddress(dictAdd);
        _dictInit(dict, type, privData);
        return dict;
    }

    /**
     * 缩小给定字典
     * 让它的已用节点数和字典大小之间的比率接近 1:1
     *
     * @param d
     * @return 成功创建体积更小的 ht[1] ，可以开始 resize 时，返回 DICT_OK；DICT_ERR 表示字典已经在 rehash ，或者 dict_can_resize 为假
     */
    public static int dictResize(Dict d) {
        int minimal;

        // 不能在关闭 rehash 或者正在 rehash 的时候调用
        if (RedisConstant.DICT_CAN_RESIZE == 0 || dictIsRehashing(d))
            return RedisConstant.DICT_ERR;

        // 计算让比率接近 1：1 所需要的最少节点数量
        minimal = d.getHt(0).getUsed();
        if (minimal < RedisConstant.DICT_HT_INITIAL_SIZE)
            minimal = RedisConstant.DICT_HT_INITIAL_SIZE;

        // 调整字典的大小
        return dictExpand(d, minimal);
    }

    /**
     * 创建一个新的哈希表，并根据字典的情况，选择以下其中一个动作来进行：
     * 1) 如果字典的 0 号哈希表为空，那么将新哈希表设置为 0 号哈希表
     * 2) 如果字典的 0 号哈希表非空，那么将新哈希表设置为 1 号哈希表，
     * 并打开字典的 rehash 标识，使得程序可以开始对字典进行 rehash
     *
     * @param d
     * @param size
     * @return size 参数不够大，或者 rehash 已经在进行时，返回 DICT_ERR；成功创建 0 号哈希表，或者 1 号哈希表时，返回 DICT_OK
     */
    public static int dictExpand(Dict d, int size) {
        // 新哈希表
        DictHt n = new DictHt();

        // 根据 size 参数，计算哈希表的大小
        int realsize = _dictNextPower(size);

        // 不能在字典正在 rehash 时进行
        // size 的值也不能小于 0 号哈希表的当前已使用节点
        if (dictIsRehashing(d) || d.getHt(0).getUsed() > size)
            return RedisConstant.DICT_ERR;

        // 为哈希表分配空间，并将所有指针指向 NULL
        n.setSize(realsize);
        n.setSizeMask(realsize - 1);
        n.setUsed(0);
        //        n.table = zcalloc(realsize*sizeof(dictEntry*));
        // n.setAddress();

        // 如果 0 号哈希表为空，那么这是一次初始化：
        // 程序将新哈希表赋给 0 号哈希表的指针，然后字典就可以开始处理键值对了。
        if (d.getHt(0).getTable() == null) {
            d.setHt(0, n.getAddress());
            return RedisConstant.DICT_OK;
        }

        // 如果 0 号哈希表非空，那么这是一次 rehash ：
        // 程序将新哈希表设置为 1 号哈希表，
        // 并将字典的 rehash 标识打开，让程序可以开始对字典进行 rehash
        d.setHt(1, n.getAddress());
        d.setRehashidx(0);
        return RedisConstant.DICT_OK;
    }

    /**
     * 执行 N 步渐进式 rehash 。
     * 返回 1 表示仍有键需要从 0 号哈希表移动到 1 号哈希表，
     * 返回 0 则表示所有键都已经迁移完毕。
     * 注意，每步 rehash 都是以一个哈希表索引（桶）作为单位的，
     * 一个桶里可能会有多个节点，
     * 被 rehash 的桶里的所有节点都会被移动到新哈希表。
     *
     * @param d
     * @param n
     * @return
     */
    public static int dictRehash(Dict d, int n) {
        // 只可以在 rehash 进行中时执行
        if (!dictIsRehashing(d))
            return 0;

        // 进行 N 步迁移
        while (n-- != 0) {
            // 如果 0 号哈希表为空，那么表示 rehash 执行完毕
            if (d.getHt(0).getUsed() == 0) {
                // 释放 0 号哈希表
                //                zfree(d->ht[0].table);

                // 将原来的 1 号哈希表设置为新的 0 号哈希表
                d.setHt(0, d.getHt(1).getAddress());

                // 重置旧的 1 号哈希表
                _dictReset(d.getHt(1));

                // 关闭 rehash 标识
                d.setRehashidx(-1);

                // 返回 0 ，向调用者表示 rehash 已经完成
                return 0;
            }

            // 确保 rehashidx 没有越界
            assert d.getHt(0).getSize() > d.getRehashidx();

            // 略过数组中为空的索引，找到下一个非空索引
            while (d.getHt(0).getTable()[d.getRehashidx()] == null)
                d.setRehashidx(d.getRehashidx() + 1);

            // 指向该索引的链表表头节点
            DictEntry de = d.getHt(0).getTable()[d.getRehashidx()];

            // 将链表中的所有节点迁移到新哈希表
            while (de != null) {
                // 保存下个节点的指针
                DictEntry nextde = de.getNext();

                // 计算新哈希表的哈希值，以及节点插入的索引位置
                int h = dictHashKey(d, de.getKey()) & d.getHt(1).getSizeMask();

                // 插入节点到新哈希表
                de.setNext(d.getHt(1).getTable()[h]);
                d.getHt(1).getTable()[h].setAddress(de.getAddress());

                // 更新计数器
                d.getHt(0).setUsed(d.getHt(0).getUsed() - 1);
                d.getHt(1).setUsed(d.getHt(1).getUsed() + 1);

                // 继续处理下个节点
                de.setAddress(nextde.getAddress());
            }
            // 将刚迁移完的哈希表索引的指针设为空
            d.getHt(0).getTable()[d.getRehashidx()].setAddress(0);

            // 更新 rehash 索引
            d.setRehashidx(d.getRehashidx() + 1);
        }
        return 1;
    }

    /**
     * 返回以毫秒为单位的 UNIX 时间戳
     *
     * @return
     */
    public static long timeInMilliseconds() {
        return System.currentTimeMillis();
    }

    /**
     * 在给定毫秒数内，以 100 步为单位，对字典进行 rehash
     *
     * @param d
     * @param ms
     * @return
     */
    public static int dictRehashMilliseconds(Dict d, int ms) {
        // 记录开始时间
        long start = timeInMilliseconds();
        int rehashes = 0;

        while (dictRehash(d, 100) != 0) {
            rehashes += 100;
            // 如果时间已过，跳出
            if (timeInMilliseconds() - start > ms)
                break;
        }

        return rehashes;
    }

    /**
     * 在字典不存在安全迭代器的情况下，对字典进行单步 rehash 。
     * <p>
     * 字典有安全迭代器的情况下不能进行 rehash ，
     * 因为两种不同的迭代和修改操作可能会弄乱字典。
     * <p>
     * 这个函数被多个通用的查找、更新操作调用，
     * 它可以让字典在被使用的同时进行 rehash 。
     *
     * @param d
     */
    private static void _dictRehashStep(Dict d) {
        if (d.getIterators() == 0)
            dictRehash(d, 1);
    }

    /**
     * 尝试将给定键值对添加到字典中
     * 只有给定键 key 不存在于字典时，添加操作才会成功
     *
     * @param d
     * @param key
     * @param val
     * @return 添加成功返回 DICT_OK ，失败返回 DICT_ERR
     */
    public static int dictAdd(Dict d, BaseStruct key, BaseStruct val) {
        // 尝试添加键到字典，并返回包含了这个键的新哈希节点
        DictEntry entry = dictAddRaw(d, key);

        // 键已存在，添加失败
        if (entry == null)
            return RedisConstant.DICT_ERR;

        // 键不存在，设置节点的值
        dictSetVal(d, entry, val);

        // 添加成功
        return RedisConstant.DICT_OK;
    }

    /**
     * 尝试将键插入到字典中
     * <p>
     * 如果键已经在字典存在，那么返回 NULL
     * <p>
     * 如果键不存在，那么程序创建新的哈希节点，
     * 将节点和键关联，并插入到字典，然后返回节点本身。
     *
     * @param d
     * @param key
     * @return
     */
    public static DictEntry dictAddRaw(Dict d, BaseStruct key) {
        // 如果条件允许的话，进行单步 rehash
        if (dictIsRehashing(d)) {
            _dictRehashStep(d);
        }

        // 计算键在哈希表中的索引值
        // 如果值为 -1 ，那么表示键已经存在
        int index = 0;
        if ((index = _dictKeyIndex(d, key)) == -1) {
            return null;
        }

        // 如果字典正在 rehash ，那么将新键添加到 1 号哈希表
        // 否则，将新键添加到 0 号哈希表
        DictHt ht = dictIsRehashing(d) ? d.getHt(1) : d.getHt(0);

        // 为新节点分配空间
        DictEntry entry = new DictEntry();
        long address = MemoryManager.alloc(MemoryManager.sizeOf(entry));

        if (address == 0)
            return null;

        // 将新节点插入到链表表头
        entry.setNext(ht.getTable()[index]);
        ht.getTable()[index].setAddress(entry.getAddress());

        // 更新哈希表已使用节点数量
        ht.setUsed(ht.getUsed() + 1);

        // 设置新节点的键
        dictSetKey(d, entry, key);

        return entry;
    }

    /**
     * 将给定的键值对添加到字典中，如果键已经存在，那么删除旧有的键值对。
     *
     * @param d
     * @param key
     * @param val
     * @return 如果键值对为全新添加，那么返回 1；如果键值对是通过对原有的键值对更新得来的，那么返回 0
     */
    public static int dictReplace(Dict d, BaseStruct key, BaseStruct val) {
        DictEntry entry, auxentry;

        //        // 尝试直接将键值对添加到字典
        //        // 如果键 key 不存在的话，添加会成功
        //        if (dictAdd(d, key, val) == RedisConstant.DICT_OK)
        //            return 1;
        //
        //        // 运行到这里，说明键 key 已经存在，那么找出包含这个 key 的节点
        //        entry = dictFind(d, key);
        //
        //        // 先保存原有的值的指针
        //        auxentry = *entry;
        //        // 然后设置新的值
        //        // T = O(1)
        //        dictSetVal(d, entry, val);
        //        // 然后释放旧值
        //        // T = O(1)
        //        dictFreeVal(d, &auxentry);

        return 0;
    }

    /**
     * dictAddRaw() 根据给定 key 释放存在，执行以下动作：
     * <p>
     * 1) key 已经存在，返回包含该 key 的字典节点
     * 2) key 不存在，那么将 key 添加到字典
     * <p>
     * 不论发生以上的哪一种情况，
     * dictAddRaw() 都总是返回包含给定 key 的字典节点。
     *
     * @param d
     * @param key
     * @return
     */
    public static DictEntry dictReplaceRaw(Dict d, BaseStruct key) {
        //        // 使用 key 在字典中查找节点
        //        dictEntry * entry = dictFind(d, key);
        //
        //        // 如果节点找到了直接返回节点，否则添加并返回一个新节点
        //        // T = O(N)
        //        return entry ? entry : dictAddRaw(d, key);

        return null;
    }

    /**
     * 查找并删除包含给定键的节点
     * <p>
     * 参数 nofree 决定是否调用键和值的释放函数
     * 0 表示调用，1 表示不调用
     *
     * @param d
     * @param key
     * @param nofree
     * @return 找到并成功删除返回 DICT_OK ，没找到则返回 DICT_ERR
     */
    public static int dictGenericDelete(Dict d, final BaseStruct key, int nofree) {
        //         int h, idx;
        //        DictEntry he, prevHe;
        //        int table;
        //
        //        // 字典（的哈希表）为空
        //        if (d->ht[0].size == 0) return RedisConstant.DICT_ERR; /* d->ht[0].table is NULL */
        //
        //        // 进行单步 rehash ，T = O(1)
        //        if (dictIsRehashing(d)) _dictRehashStep(d);
        //
        //        // 计算哈希值
        //        h = dictHashKey(d, key);
        //
        //        // 遍历哈希表
        //        // T = O(1)
        //        for (table = 0; table <= 1; table++) {
        //
        //            // 计算索引值
        //            idx = h & d->ht[table].sizemask;
        //            // 指向该索引上的链表
        //            he = d->ht[table].table[idx];
        //            prevHe = NULL;
        //            // 遍历链表上的所有节点
        //            // T = O(1)
        //            while(he) {
        //
        //                if (dictCompareKeys(d, key, he->key)) {
        //                    // 超找目标节点
        //
        //                /* Unlink the element from the list */
        //                    // 从链表中删除
        //                    if (prevHe)
        //                        prevHe->next = he->next;
        //                    else
        //                        d->ht[table].table[idx] = he->next;
        //
        //                    // 释放调用键和值的释放函数？
        //                    if (!nofree) {
        //                        dictFreeKey(d, he);
        //                        dictFreeVal(d, he);
        //                    }
        //
        //                    // 释放节点本身
        //                    zfree(he);
        //
        //                    // 更新已使用节点数量
        //                    d->ht[table].used--;
        //
        //                    // 返回已找到信号
        //                    return DICT_OK;
        //                }
        //
        //                prevHe = he;
        //                he = he->next;
        //            }
        //
        //            // 如果执行到这里，说明在 0 号哈希表中找不到给定键
        //            // 那么根据字典是否正在进行 rehash ，决定要不要查找 1 号哈希表
        //            if (!dictIsRehashing(d)) break;
        //        }

        // 没找到
        return RedisConstant.DICT_ERR; /* not found */
    }

    /**
     * 从字典中删除包含给定键的节点
     * <p>
     * 并且调用键值的释放函数来删除键值
     *
     * @return 找到并成功删除返回 DICT_OK ，没找到则返回 DICT_ERR
     */
    public int dictDelete(Dict ht, final BaseStruct key) {
        return dictGenericDelete(ht, key, 0);
    }

    /**
     * 从字典中删除包含给定键的节点
     * <p>
     * 但不调用键值的释放函数来删除键值
     *
     * @param ht
     * @param key
     * @return 找到并成功删除返回 DICT_OK ，没找到则返回 DICT_ERR
     */
    public int dictDeleteNoFree(Dict ht, final BaseStruct key) {
        return dictGenericDelete(ht, key, 1);
    }

    /**
     * 删除哈希表上的所有节点，并重置哈希表的各项属性
     *
     * @return
     */
    private static int _dictClear(Dict d, DictHt ht, Function callback) {
        long i;

        //        // 遍历整个哈希表
        //        // T = O(N)
        //        for (i = 0; i < ht->size && ht->used > 0; i++) {
        //            dictEntry *he, *nextHe;
        //
        //            if (callback && (i & 65535) == 0) callback(d->privdata);
        //
        //            // 跳过空索引
        //            if ((he = ht->table[i]) == NULL) continue;
        //
        //            // 遍历整个链表
        //            // T = O(1)
        //            while(he) {
        //                nextHe = he->next;
        //                // 删除键
        //                dictFreeKey(d, he);
        //                // 删除值
        //                dictFreeVal(d, he);
        //                // 释放节点
        //                zfree(he);
        //
        //                // 更新已使用节点计数
        //                ht->used--;
        //
        //                // 处理下个节点
        //                he = nextHe;
        //            }
        //        }
        //
        //        // 释放哈希表结构
        //        zfree(ht->table);
        //
        //        // 重置哈希表属性
        //        _dictReset(ht);

        return RedisConstant.DICT_OK;
    }

    /**
     * 删除并释放整个字典
     *
     * @param d
     */
    public static void dictRelease(Dict d) {
        // 删除并清空两个哈希表
        //        _dictClear(d,&d->ht[0],NULL);
        //        _dictClear(d,&d->ht[1],NULL);
        //        // 释放节点结构
        //        zfree(d);
    }

    /**
     * 返回字典中包含键 key 的节点
     *
     * @param key
     * @return 找到返回节点，找不到返回 NULL
     */
    public static DictEntry dictFind(Dict d, final BaseStruct key) {
        //        dictEntry *he;
        //        unsigned int h, idx, table;
        //
        //        // 字典（的哈希表）为空
        //        if (d->ht[0].size == 0) return null;
        //
        //        // 如果条件允许的话，进行单步 rehash
        //        if (dictIsRehashing(d)) _dictRehashStep(d);
        //
        //        // 计算键的哈希值
        //        h = dictHashKey(d, key);
        //        // 在字典的哈希表中查找这个键
        //        // T = O(1)
        //        for (table = 0; table <= 1; table++) {
        //
        //            // 计算索引值
        //            idx = h & d->ht[table].sizemask;
        //
        //            // 遍历给定索引上的链表的所有节点，查找 key
        //            he = d->ht[table].table[idx];
        //            // T = O(1)
        //            while(he) {
        //
        //                if (dictCompareKeys(d, key, he->key))
        //                    return he;
        //
        //                he = he->next;
        //            }
        //
        //            // 如果程序遍历完 0 号哈希表，仍然没找到指定的键的节点
        //            // 那么程序会检查字典是否在进行 rehash ，
        //            // 然后才决定是直接返回 NULL ，还是继续查找 1 号哈希表
        //            if (!dictIsRehashing(d)) return NULL;
        //        }

        // 进行到这里时，说明两个哈希表都没找到
        return null;
    }

    /**
     * 获取包含给定键的节点的值
     *
     * @param d
     * @param key
     * @return 如果节点不为空，返回节点的值；否则返回 NULL
     */
    public static BaseStruct dictFetchValue(Dict d, final BaseStruct key) {
        DictEntry he = dictFind(d, key);
        //        return he != null ? dictGetVal(he) : null;
        return null;
    }

    public static long dictFingerprint(Dict d) {
        long[] integers = new long[6];
        long hash = 0;
        int j;

        //        integers[0] = (long) d->ht[0].table;
        //        integers[1] = d->ht[0].size;
        //        integers[2] = d->ht[0].used;
        //        integers[3] = (long) d->ht[1].table;
        //        integers[4] = d->ht[1].size;
        //        integers[5] = d->ht[1].used;

         /*
         * Result = hash(hash(hash(int1)+int2)+int3) ...
         */
        for (j = 0; j < 6; j++) {
            hash += integers[j];
            //            hash = (~hash) + (hash << 21); // hash = (hash << 21) - hash - 1;
            //            hash = hash ^ (hash >> 24);
            //            hash = (hash + (hash << 3)) + (hash << 8); // hash * 265
            //            hash = hash ^ (hash >> 14);
            //            hash = (hash + (hash << 2)) + (hash << 4); // hash * 21
            //            hash = hash ^ (hash >> 28);
            //            hash = hash + (hash << 31);
        }
        return hash;
    }

    /**
     * 创建并返回给定字典的不安全迭代器
     *
     * @param d
     * @return
     */
    public static DictIterator dictGetIterator(Dict d) {
        DictIterator iter = null;// zmalloc(sizeof(*iter));

        //        iter->d = d;
        //        iter->table = 0;
        //        iter->index = -1;
        //        iter->safe = 0;
        //        iter->entry = NULL;
        //        iter->nextEntry = NULL;

        return iter;
    }

    /**
     * 创建并返回给定节点的安全迭代器
     *
     * @param d
     * @return
     */
    public static DictIterator dictGetSafeIterator(Dict d) {
        DictIterator i = dictGetIterator(d);

        // 设置安全迭代器标识
        //        i->safe = 1;

        return i;
    }

    /**
     * 返回迭代器指向的当前节点
     * 字典迭代完毕时，返回 NULL
     *
     * @param iter
     * @return
     */
    public static DictEntry dictNext(DictIterator iter) {
        //        while (true) {
        //            // 进入这个循环有两种可能：
        //            // 1) 这是迭代器第一次运行
        //            // 2) 当前索引链表中的节点已经迭代完（NULL 为链表的表尾）
        //            if (iter->entry == NULL) {
        //
        //                // 指向被迭代的哈希表
        //                dictht *ht = &iter->d->ht[iter->table];
        //
        //                // 初次迭代时执行
        //                if (iter->index == -1 && iter->table == 0) {
        //                    // 如果是安全迭代器，那么更新安全迭代器计数器
        //                    if (iter->safe)
        //                        iter->d->iterators++;
        //                        // 如果是不安全迭代器，那么计算指纹
        //                    else
        //                        iter->fingerprint = dictFingerprint(iter->d);
        //                }
        //                // 更新索引
        //                iter->index++;
        //
        //                // 如果迭代器的当前索引大于当前被迭代的哈希表的大小
        //                // 那么说明这个哈希表已经迭代完毕
        //                if (iter->index >= (signed) ht->size) {
        //                    // 如果正在 rehash 的话，那么说明 1 号哈希表也正在使用中
        //                    // 那么继续对 1 号哈希表进行迭代
        //                    if (dictIsRehashing(iter->d) && iter->table == 0) {
        //                        iter->table++;
        //                        iter->index = 0;
        //                        ht = &iter->d->ht[1];
        //                        // 如果没有 rehash ，那么说明迭代已经完成
        //                    } else {
        //                        break;
        //                    }
        //                }
        //
        //                // 如果进行到这里，说明这个哈希表并未迭代完
        //                // 更新节点指针，指向下个索引链表的表头节点
        //                iter->entry = ht->table[iter->index];
        //            } else {
        //                // 执行到这里，说明程序正在迭代某个链表
        //                // 将节点指针指向链表的下个节点
        //                iter->entry = iter->nextEntry;
        //            }
        //
        //            // 如果当前节点不为空，那么也记录下该节点的下个节点
        //            // 因为安全迭代器有可能会将迭代器返回的当前节点删除
        //            if (iter->entry) {
        //            /* We need to save the 'next' here, the iterator user
        //             * may delete the entry we are returning. */
        //                iter->nextEntry = iter->entry->next;
        //                return iter->entry;
        //            }
        //        }

        // 迭代完毕
        return null;
    }

    /**
     * 释放给定字典迭代器
     *
     * @param iter
     */
    public static void dictReleaseIterator(DictIterator iter) {

        //        if (!(iter->index == -1 && iter->table == 0)) {
        //            // 释放安全迭代器时，安全迭代器计数器减一
        //            if (iter->safe)
        //                iter->d->iterators--;
        //                // 释放不安全迭代器时，验证指纹是否有变化
        //            else
        //                assert(iter->fingerprint == dictFingerprint(iter->d));
        //        }
        //        zfree(iter);
    }

    /**
     * 随机返回字典中任意一个节点。
     * <p>
     * 可用于实现随机化算法。
     * <p>
     * 如果字典为空，返回 NULL 。
     *
     * @param d
     * @return
     */
    public static DictEntry dictGetRandomKey(Dict d) {
        //        DictEntry he, orighe;
        //        int h;
        //        int listlen, listele;
        //
        //        // 字典为空
        //        if (dictSize(d) == 0)
        //            return null;
        //
        //        // 进行单步 rehash
        //        if (dictIsRehashing(d))
        //            _dictRehashStep(d);
        //
        //        // 如果正在 rehash ，那么将 1 号哈希表也作为随机查找的目标
        //        if (dictIsRehashing(d)) {
        //            // T = O(N)
        //            do {
        //                h = random() % (d -> ht[0].size + d -> ht[1].size);
        //                he = (h>=d -> ht[0].size)?d -> ht[1].table[h - d -> ht[0].size]:
        //                d -> ht[0].table[h];
        //            } while (he == NULL);
        //            // 否则，只从 0 号哈希表中查找节点
        //        } else {
        //            // T = O(N)
        //            do {
        //                h = random() & d -> ht[0].sizemask;
        //                he = d -> ht[0].table[h];
        //            } while (he == null);
        //        }
        //
        //        // 目前 he 已经指向一个非空的节点链表
        //        // 程序将从这个链表随机返回一个节点
        //        listlen = 0;
        //        orighe = he;
        //        // 计算节点数量, T = O(1)
        //        while (he) {
        //            he = he -> next;
        //            listlen++;
        //        }
        //        // 取模，得出随机节点的索引
        //        listele = random() % listlen;
        //        he = orighe;
        //        // 按索引查找节点
        //        // T = O(1)
        //        while (listele--)
        //            he = he -> next;
        //
        //        // 返回随机节点
        //        return he;
        return null;
    }

    public static int dictGetRandomKeys(Dict d, DictEntry des, int count) {
        int j;
        int stored = 0;

        //        if (dictSize(d) < count) count = dictSize(d);
        //        while(stored < count) {
        //            for (j = 0; j < 2; j++) {
        //            /* Pick a random point inside the hash table 0 or 1. */
        //                unsigned int i = random() & d->ht[j].sizemask;
        //                int size = d->ht[j].size;
        //
        //            /* Make sure to visit every bucket by iterating 'size' times. */
        //                while(size--) {
        //                    dictEntry *he = d->ht[j].table[i];
        //                    while (he) {
        //                    /* Collect all the elements of the buckets found non
        //                     * empty while iterating. */
        //                        *des = he;
        //                        des++;
        //                        he = he->next;
        //                        stored++;
        //                        if (stored == count) return stored;
        //                    }
        //                    i = (i+1) & d->ht[j].sizemask;
        //                }
        //            /* If there is only one table and we iterated it all, we should
        //             * already have 'count' elements. Assert this condition. */
        //                assert(dictIsRehashing(d) != 0);
        //            }
        //        }
        return stored;
    }

    public static long rev(long v) {
        //        unsigned long s = 8 * sizeof(v); // bit size; must be power of 2
        //        unsigned long mask = ~0;
        //        while ((s >>= 1) > 0) {
        //            mask ^= (mask << s);
        //            v = ((v >> s) & mask) | ((v << s) & ~mask);
        //        }
        //        return v;
        return 0;
    }

    /**
     * dictScan() 函数用于迭代给定字典中的元素。
     * 迭代按以下方式执行：
     * <p>
     * 1) 一开始，使用 0 作为游标来调用函数。
     * 2) 函数执行一步迭代操作，并返回一个下次迭代时使用的新游标。
     * 3) 当函数返回的游标为 0 时，迭代完成。
     * <p>
     * 函数保证，在迭代从开始到结束期间，一直存在于字典的元素肯定会被迭代到，
     * 但一个元素可能会被返回多次。
     * <p>
     * 每当一个元素被返回时，回调函数 fn 就会被执行，
     * fn 函数的第一个参数是 privdata ，而第二个参数则是字典节点 de 。
     * <p>
     * 工作原理
     * <p>
     * 迭代所使用的算法是由 Pieter Noordhuis 设计的，
     * 算法的主要思路是在二进制高位上对游标进行加法计算
     * 也即是说，不是按正常的办法来对游标进行加法计算，
     * 而是首先将游标的二进制位翻转（reverse）过来，
     * 然后对翻转后的值进行加法计算，
     * 最后再次对加法计算之后的结果进行翻转。
     * <p>
     * 这一策略是必要的，因为在一次完整的迭代过程中，
     * 哈希表的大小有可能在两次迭代之间发生改变。
     * <p>
     * 哈希表的大小总是 2 的某个次方，并且哈希表使用链表来解决冲突，
     * 因此一个给定元素在一个给定表的位置总可以通过 Hash(key) & SIZE-1
     * 公式来计算得出，
     * 其中 SIZE-1 是哈希表的最大索引值，
     * 这个最大索引值就是哈希表的 mask （掩码）。
     * <p>
     * 举个例子，如果当前哈希表的大小为 16 ，
     * 那么它的掩码就是二进制值 1111 ，
     * 这个哈希表的所有位置都可以使用哈希值的最后四个二进制位来记录。
     * <p>
     * WHAT HAPPENS IF THE TABLE CHANGES IN SIZE?
     * 如果哈希表的大小改变了怎么办？
     * <p>
     * 当对哈希表进行扩展时，元素可能会从一个槽移动到另一个槽，
     * 举个例子，假设我们刚好迭代至 4 位游标 1100 ，
     * 而哈希表的 mask 为 1111 （哈希表的大小为 16 ）。
     * <p>
     * 如果这时哈希表将大小改为 64 ，那么哈希表的 mask 将变为 111111 ，在 rehash 的时候可是会出现两个哈希表的阿！
     * <p>
     * 限制
     * <p>
     * 这个迭代器是完全无状态的，这是一个巨大的优势，因为迭代可以在不使用任何额外内存的情况下进行。
     * <p>
     * 这个设计的缺陷在于：
     * 1) 函数可能会返回重复的元素，不过这个问题可以很容易在应用层解决。
     * 2) 为了不错过任何元素，迭代器需要返回给定桶上的所有键，以及因为扩展哈希表而产生出来的新表，所以迭代器必须在一次迭代中返回多个元素。
     * 3) 对游标进行翻转（reverse）的原因初看上去比较难以理解，不过阅读这份注释应该会有所帮助。
     *
     * @param d
     * @param v
     * @param fn
     * @param privdata
     * @return
     */
    public static long dictScan(Dict d, long v, Function fn, Object privdata) {
        DictHt t0, t1;
        final DictEntry de;
        long m0, m1;

        //        // 跳过空字典
        //        if (dictSize(d) == 0)
        //            return 0;
        //
        //        // 迭代只有一个哈希表的字典
        //        if (!dictIsRehashing(d)) {
        //
        //            // 指向哈希表
        //            t0 =&(d -> ht[0]);
        //
        //            // 记录 mask
        //            m0 = t0 -> sizemask;
        //
        //            // 指向哈希桶
        //            de = t0 -> table[v & m0];
        //            // 遍历桶中的所有节点
        //            while (de) {
        //                fn(privdata, de);
        //                de = de -> next;
        //            }
        //
        //            // 迭代有两个哈希表的字典
        //        } else {
        //
        //            // 指向两个哈希表
        //            t0 =&d -> ht[0];
        //            t1 =&d -> ht[1];
        //
        //            // 确保 t0 比 t1 要小
        //            if (t0 -> size > t1 -> size) {
        //                t0 =&d -> ht[1];
        //                t1 =&d -> ht[0];
        //            }
        //
        //            // 记录掩码
        //            m0 = t0 -> sizemask;
        //            m1 = t1 -> sizemask;
        //
        //            // 指向桶，并迭代桶中的所有节点
        //            de = t0 -> table[v & m0];
        //            while (de) {
        //                fn(privdata, de);
        //                de = de -> next;
        //            }
        //
        //            // 迭代大表中的桶
        //            // 这些桶被索引的 expansion 所指向
        //            do {
        //                // 指向桶，并迭代桶中的所有节点
        //                de = t1 -> table[v & m1];
        //                while (de) {
        //                    fn(privdata, de);
        //                    de = de -> next;
        //                }
        //
        //                v = (((v | m0) + 1) & ~m0) | (v & m0);
        //
        //            } while (v & (m0 ^ m1));
        //        }
        //
        //        v |= ~m0;
        //
        //        v = rev(v);
        //        v++;
        //        v = rev(v);
        //
        //        return v;
        return 0;
    }

    //----  私有协议

    /**
     * 根据需要，初始化字典（的哈希表），或者对字典（的现有哈希表）进行扩展
     *
     * @param d
     * @return
     */
    private static int _dictExpandIfNeeded(Dict d) {
        // 渐进式 rehash 已经在进行了，直接返回
        if (dictIsRehashing(d))
            return RedisConstant.DICT_OK;

        // 如果字典（的 0 号哈希表）为空，那么创建并返回初始化大小的 0 号哈希表
        if (d.getHt(0).getSize() == 0)
            return dictExpand(d, RedisConstant.DICT_HT_INITIAL_SIZE);

        // 以下两个条件之一为真时，对字典进行扩展
        // 1）字典已使用节点数和字典大小之间的比率接近 1：1 并且 dict_can_resize 为真
        // 2）已使用节点数和字典大小之间的比率超过 dict_force_resize_ratio
        if (d.getHt(0).getUsed() >= d.getHt(0).getSize() && (RedisConstant.DICT_CAN_RESIZE != 0
                || d.getHt(0).getUsed() / d.getHt(0).getSize()
                > RedisConstant.DICT_FORCE_RESIZE_RATIO)) {
            // 新哈希表的大小至少是目前已使用节点数的两倍
            return dictExpand(d, d.getHt(0).getUsed() * 2);
        }

        return RedisConstant.DICT_OK;
    }

    /**
     * 计算第一个大于等于 size 的 2 的 N 次方，用作哈希表的值
     *
     * @param size
     * @return
     */
    private static int _dictNextPower(int size) {
        if (size >= Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }

        int i = RedisConstant.DICT_HT_INITIAL_SIZE;
        while (true) {
            if (i >= size)
                return i;
            i *= 2;
        }
    }

    /**
     * 返回可以将 key 插入到哈希表的索引位置
     * 如果 key 已经存在于哈希表，那么返回 -1
     * <p>
     * 如果字典正在进行 rehash ，那么总是返回 1 号哈希表的索引。
     * 因为在字典进行 rehash 时，新节点总是插入到 1 号哈希表。
     *
     * @param d
     * @param key
     * @return
     */
    private static int _dictKeyIndex(Dict d, final BaseStruct key) {
        int h = 0, idx = 0, table = 0;
        DictEntry he = null;

        // 单步 rehash
        if (_dictExpandIfNeeded(d) == RedisConstant.DICT_ERR)
            return -1;

        // 计算 key 的哈希值
        h = dictHashKey(d, key);
        for (table = 0; table <= 1; table++) {
            // 计算索引值
            idx = h & d.getHt(table).getSizeMask();

            // 查找 key 是否存在
            he = d.getHt(table).getTable()[idx];
            while (he != null) {
                if (dictCompareKeys(d, key, he.getKey()))
                    return -1;
                he = he.getNext();
            }

            // 如果运行到这里时，说明 0 号哈希表中所有节点都不包含 key
            // 如果这时 rehahs 正在进行，那么继续对 1 号哈希表进行 rehash
            if (!dictIsRehashing(d))
                break;
        }

        // 返回索引值
        return idx;
    }

    /**
     * 初始化哈希表
     *
     * @return
     */
    private static int _dictInit(Dict d, DictType type, Object privData) {
        // 初始化两个哈希表的各项属性值，但暂时还不分配内存给哈希表数组
        d.setHt(0, d.getAddress() + d.getHtOffset(0));
        d.setHt(1, d.getAddress() + d.getHtOffset(1));
        //默认即为0，可以省略
        //        _dictReset(d.getHt(0));
        //        _dictReset(d.getHt(1));

        // 设置类型特定函数
        d.setType(type);
        // 设置私有数据
        d.setPrivData(privData);
        // 设置哈希表 rehash 状态
        d.setRehashidx(-1);
        // 设置字典的安全迭代器数量
        d.setIterators(0);

        return RedisConstant.DICT_OK;
    }

    private static void _dictReset(DictHt ht) {
        //设置默认值
        ht.setTable(0);
        ht.setSize(0);
        ht.setSizeMask(0);
        ht.setUsed(0);
    }

    public static int dictIntHashFunction(int key) {
        key += ~(key << 15);
        key ^= (key >> 10);
        key += (key << 3);
        key ^= (key >> 6);
        key += ~(key << 11);
        key ^= (key >> 16);
        return key;
    }

    public static int dictIdentityHashFunction(int key) {
        return key;
    }

    //-----  macro

    // 释放给定字典节点的值
    private static void dictFreeVal(Dict d, DictEntry entry) {
        if (d.getType() != null)
            d.getType().valDestructor(d.getPrivData(), entry.getValue());
    }

    // 设置给定字典节点的值
    private static void dictSetVal(Dict d, DictEntry entry, BaseStruct val) {
        if (d.getType() != null)
            entry.setValue(d.getType().valDup(d.getPrivData(), val));
        else
            entry.setValue(val);
    }

    // 将一个有符号整数设为节点的值
    private static void dictSetSignedIntegerVal(DictEntry entry, BaseStruct val) {
        entry.setValue(val);
    }

    // 将一个无符号整数设为节点的值
    private static void dictSetUnsignedIntegerVal(DictEntry entry, BaseStruct val) {
        entry.setValue(val);
    }

    // 释放给定字典节点的键
    private static void dictFreeKey(Dict d, DictEntry entry) {
        if (d.getType() != null)
            d.getType().keyDestructor(d.getPrivData(), entry.getKey());
    }

    // 设置给定字典节点的键
    private static void dictSetKey(Dict d, DictEntry entry, BaseStruct key) {
        if (d.getType() != null)
            entry.setKey(d.getType().keyDup(d.getPrivData(), key));
        else
            entry.setKey(key);
    }

    // 比对两个键
    private static boolean dictCompareKeys(Dict d, BaseStruct key1, BaseStruct key2) {
        return d.getType() != null ?
                d.getType().keyCompare(d.getPrivData(), key1, key2) :
                (key1 == key2);
    }

    // 计算给定键的哈希值
    private static int dictHashKey(Dict d, BaseStruct key) {
        return d.getType().hashFunction(key);
    }

    // 返回获取给定节点的键
    private static BaseStruct dictGetKey(DictEntry he) {
        return he.getKey();
    }

    // 返回获取给定节点的值
    private static BaseStruct dictGetVal(DictEntry he) {
        return he.getValue();
    }

    // 返回获取给定节点的有符号整数值
    private static BaseStruct dictGetSignedIntegerVal(DictEntry he) {
        return he.getValue();
    }

    // 返回给定节点的无符号整数值
    private static BaseStruct dictGetUnsignedIntegerVal(DictEntry he) {
        return he.getValue();
    }

    // 返回给定字典的大小
    private static int dictSlots(Dict d) {
        return d.getHt(0).getSize() + d.getHt(1).getSize();
    }

    // 返回字典的已有节点数量
    private static int dictSize(Dict d) {
        return d.getHt(0).getUsed() + d.getHt(1).getUsed();
    }

    // 查看字典是否正在 rehash
    private static boolean dictIsRehashing(Dict ht) {
        return ht.getRehashidx() != -1;
    }
}
