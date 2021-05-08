This mod adds admin commands for pruning region and poi files which do not have any chunks claimed by ftb-chunks.

## Commands

### prune region\_files
   
Prunes all **REGION** files of a world-level/dimension which do not contain any chunks claimed by ftb-chunks.

**Syntax**: /prune region\_files [dimension &lt;dimension&gt;] [doNotBackup &lt;true | false&gt;]

#### Parameters
- **dimension**
The world level/dimension to prune the data from. **default:** overworld
- **do\_not\_backup**
If set to true, pruned files will not be backed up. **default:** false
Use with extreme caution! It will permanently delete files.

### prune poi\_files
Prunes all **POI** files of a world-level/dimension which do not contain any chunks claimed by ftb-chunks.

**Syntax**: /prune poi\_files [dimension &lt;dimension&gt;] [doNotBackup &lt;true | false&gt;]

#### Parameters
- **dimension**
The world level/dimension to prune the data from. **default;** overworld
- **do\_not\_backup**
If set to true, pruned files will not be backed up. **default:** false
Use with extreme caution! It will permanently delete files.


Temporary logo by smashicons
