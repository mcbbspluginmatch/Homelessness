/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness.api.module;

import java.io.File;
import java.util.List;

/**
 *
 * @author BergStudio
 */
public abstract interface FileManagement {
    
    /**
     * 保存 Management 中的所有对象为文件
     * 
     */
    public abstract void save();

    /**
     * 保存 Management 中标识为 paramString 的对象为文件
     * 
     * @param paramString
     */
    public abstract void save(String paramString);
    
    /**
     * 删除 Management 中标识为 paramString 的对象和对应文件 这将使对象直接消失
     * 实现时应当注意删除后可能带来的后果 并且做好解决方案
     * 
     * @param paramString
     */
    public abstract void delete(String paramString);
    
    /**
     * 删除 Management 中的所有对象和文件
     */
    public abstract void deleteAll();
    
    /**
     * 从 paramFile 中读取一个文件
     * @param paramFile 从此读取
     */
    public abstract void read(File paramFile);
    
    /**
     * 从 Management 中所指定的子目录寻找名称为 paramString 的文件并且读取
     * 
     * @param paramString 文件名
     */
    public abstract void read(String paramString);
}
