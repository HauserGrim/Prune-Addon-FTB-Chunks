This mod adds admin commands for pruning region and poi files which do not have any chunks claimed by ftb-chunks.

## Commands

### prune
   
Prunes region and poi files of a dimension.  
Removes all the files which do not contain any chunks claimed by FTB-Chunks.

**Syntax**: /prune [region_files | poi_files] [dimension &lt;dimension&gt;] [doNotBackup &lt;true | false&gt;]

#### Parameters
-  **file-type:**  
    The types of files which should be pruned, possible values are **region_files** and **poi_files**
    - **default**:both      
  
-  **dimension:**
    The dimension to prune the data from. 
   - **default:** overworld
-  **do\_not\_backup:**
    If set to true, pruned files will not be backed up. 
   - **default:** false   
Use with extreme caution! It will permanently delete files.
  
Temporary logo by smashicons
