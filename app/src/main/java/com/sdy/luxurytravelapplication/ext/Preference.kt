package com.sdy.luxurytravelapplication.ext

import android.annotation.SuppressLint
import com.blankj.utilcode.util.SPUtils
import com.sdy.luxurytravelapplication.constant.Constants
import java.io.*
import kotlin.reflect.KProperty

/**
 *    author : ZFM
 *    date   : 2021/3/1617:23
 *    desc   :
 *    version: 1.0
 */
class Preference<T>(val name: String, private val default: T) {

    companion object {
        private val file_name = Constants.SPNAME

        private val prefs: SPUtils by lazy {
            SPUtils.getInstance(file_name)
        }

        /**
         * 删除全部数据
         */
        fun clearPreference() {
            prefs.clear()
        }

        /**
         * 根据key删除存储数据
         */
        fun clearPreference(key: String) {
            prefs.remove(key)
        }

        /**
         * 查询某个key是否已经存在
         *
         * @param key
         * @return
         */
        fun contains(key: String): Boolean {
            return prefs.contains(key)
        }

        /**
         * 返回所有的键值对
         *
         * @param context
         * @return
         */
        fun getAll(): Map<String, *> {
            return prefs.all
        }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return getSharedPreferences(name, default)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putSharedPreferences(name, value)
    }

    @SuppressLint("CommitPrefEdits")
    private fun putSharedPreferences(name: String, value: T) = with(prefs) {
        when (value) {
            is Long -> put(name, value)
            is String -> put(name, value)
            is Int -> put(name, value)
            is Boolean -> put(name, value)
            is Float -> put(name, value)
            else -> put(name, serialize(value))
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getSharedPreferences(name: String, default: T): T = with(prefs) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)?:""
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> deSerialization(getString(name, serialize(default))?:"")
        }
        return res as T
    }

    /**
     * 序列化对象

     * @param person
     * *
     * @return
     * *
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun <A> serialize(obj: A): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(
            byteArrayOutputStream)
        objectOutputStream.writeObject(obj)
        var serStr = byteArrayOutputStream.toString("ISO-8859-1")
        serStr = java.net.URLEncoder.encode(serStr, "UTF-8")
        objectOutputStream.close()
        byteArrayOutputStream.close()
        return serStr
    }

    /**
     * 反序列化对象

     * @param str
     * *
     * @return
     * *
     * @throws IOException
     * *
     * @throws ClassNotFoundException
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IOException::class, ClassNotFoundException::class)
    private fun <A> deSerialization(str: String): A {
        val redStr = java.net.URLDecoder.decode(str, "UTF-8")
        val byteArrayInputStream = ByteArrayInputStream(
            redStr.toByteArray(charset("ISO-8859-1")))
        val objectInputStream = ObjectInputStream(
            byteArrayInputStream)
        val obj = objectInputStream.readObject() as A
        objectInputStream.close()
        byteArrayInputStream.close()
        return obj
    }

}
