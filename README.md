# AndroidResourceCleaner

自动清理Android项目的无用资源。配合AndroidUnusedResources.jar使用，使用AndroidUnusedResources检测无用资源并导出到文本文件，再使用cleaner.jar解析文本文件，自动删除文本文件中列出的无用资源。

用法示例：  
```
  java -jar AndroidUnusedResources.jar >del.txt
  java -jar cleaner.jar del.txt
```
  
 1. 不会清理.id资源
 2. 对于attr文件的清理不能做出保证，虽然不会误删，但是会有一些资源无法删除，这是AndroidUnusedResources.jar的输出 **有可能** 不正确导致的。用AndroidUnusedResources1.6.2测试时发现styleable输出位置不对，尼玛……
