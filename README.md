<h2><strong>Prune Addon</strong></h2>
<p><span style="font-size: 14px;">This mod adds admin commands to remove non-claimed chunks from saved game files.</span></p>

<h2><strong>Possible Usage</strong></h2>
<ul>
<li><span style="font-size: 14px;"><strong><b>Regenerating&nbsp;chunks without having to reset the world.</b></strong></span></li>
<li><span style="font-size: 14px;"><strong>Reducing the size of backup files.</strong></span></li>
<li><span style="font-size: 14px;"><strong>Reducing the disk size of ridiculously large worlds.&nbsp;</strong></span></li>
<li><span style="font-size: 14px;"><strong>Getting rid of abandoned bases on multiplayer servers.</strong></span></li>
</ul>
<p>&nbsp;</p>
<h2 id="commands"><strong>Prune Command</strong></h2>
<p><span style="font-size: 14px;">Prunes region and poi files of a dimension.</span><br /><span style="font-size: 14px;">Removes all the files which do not contain any chunks claimed by FTB-Chunks.</span></p>
<p><span style="font-size: 14px;"><strong>Syntax</strong>: /prune [-deep &lt;true/false&gt;] [-filetype &lt;region_files | poi_files&gt;] [-dimension &lt;dimension&gt;] [do_not_backup &lt;true | false&gt;]</span></p>
<h4 id="parameters"><span style="font-size: 14px;">Parameters</span></h4>
<ul>
<li><span style="font-size: 14px;"><strong style="font-size: 1.2rem;">deep: when set to true, the mod will go through each file containing claimed chunks and will manually remove all the adjacent non-claimed chunks. </strong><strong style="font-size: 1.2rem;">default</strong><strong style="font-size: 1.2rem;">: false</strong></span></li>
<li><span style="font-size: 14px;"><strong style="font-size: 1.2rem;">file-type:&nbsp;</strong>The types of files which should be pruned, possible values are <strong style="font-size: 1.2rem;">region_files</strong> and <strong style="font-size: 1.2rem;">poi_files,&nbsp;</strong><strong style="font-size: 1.2rem;">default</strong>:both</span></li>
<li><span style="font-size: 14px;"><strong style="font-size: 1.2rem;">dimension:</strong> The dimension to prune the data from.&nbsp;<strong>default:</strong> overworld</span></li>
<li><span style="font-size: 14px;"><strong>do_not_backup:</strong> If set to true, pruned files will not be backed up.&nbsp;<strong>default:</strong> false</span><br /><span style="font-size: 14px;">&nbsp; &nbsp; &nbsp;Use with extreme caution! <strong>true</strong> will permanently delete files.</span></li>
</ul>
<p>&nbsp;</p>
<h2 id="commands"><strong>Instructions for Pruning</strong></h2>
<p><span style="font-size: 14px;"><strong>Make sure&nbsp; of the following</strong></span></p>
<ul>
<li><span style="font-size: 14px;">Make sure that all the chunks which you want to purge are unloaded.</span></li>
<li><span style="font-size: 14px;"><b>You are not in a chunk which you want to be pruned.,</b></span></li>
<li><span style="font-size: 14px;">If multiplayer, no other players are in chunks to be purged.</span></li>
<li><span style="font-size: 14px;">Restart the server after the prune command.</span></li>
<li><span style="font-size: 14px; color: #993300;" data-darkreader-inline-color="">If you see the same files in origin or poi folder as the files in the backup origin and poi folder respectively, then something went wrong. The files you wanted to be purged had loaded chunks on the server so the server saved them again after they were pruned. You can bypass this by killing the server instantly after executing the prune command and preventing the server from saving&nbsp; loaded chunks. Be careful because you can lose unsaved progress this way.</span></li>
</ul>
<p>&nbsp;</p>
<h2 style="color: #993300;" data-darkreader-inline-color=""><strong>Deep pruning implemented!<br /></strong></h2>
<p><span style="font-size: 14px;"><b>You can now prune all unclaimed chunks with -deep parameter! Even the ones saved in same region files as claimed chunks.<br /></b></span></p>
<p>&nbsp;</p>
<h2><strong>Planned Features</strong></h2>
<ul>
<li><b>Configurable&nbsp;paths for backup location of pruned files,</b></li>
<li><b>Compression of files to be backed up,</b></li>
<li>Automated<b>&nbsp;</b>scheduled<b>&nbsp;pruning,</b></li>
<li><del><b>Pruning of non-claimed chunks stored in the same region file as claimed chunks.&nbsp; </b></del><strong><b>DONE!!</b></strong><del><b><br /></b></del></li>
</ul>
<p>&nbsp;</p>
<h2 id="commands"><strong>Distribution</strong></h2>
<p><span style="font-size: 14px;">Feel free to use this in your modpack.&nbsp;</span><br /><span style="font-size: 14px;">Any form of distribution should either be via this CurseForge project or should include a link to this description page.</span></p>
<p><span style="font-size: 14px;">&nbsp;</span></p>
<p><span style="font-size: 14px;">Join our discord to get access to the play/test Starpack server.</span></p>
<p><a href="https://discord.gg/s4VpDQHCTa"><img src="https://discordapp.com/api/guilds/743605058217836576/widget.png?style=banner3" /></a></p>
<p>&nbsp;</p>
