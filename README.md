# AndroidResourceCleaner

自动清理Android项目的无用资源。配合AndroidUnusedResources.jar使用，先将代码导出为jar文件，如cleaner.jar,再使用AndroidUnusedResources检测无用资源并导出到文本文件，再使用cleaner.jar解析文本文件，自动删除文本文件中列出的无用资源。

用法示例：  
```
  java -jar AndroidUnusedResources.jar >del.txt
  java -jar cleaner.jar del.txt
```
  
不会清理.id资源


### 仅支持UTF-8编码的工程，非UTF-8的工程没有测试过，如非UTF-8编码的工程，请自行修改源码内的 encoding


有空再添加Android Studio支持
