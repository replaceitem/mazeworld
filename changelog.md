## Warning: The internal data structures has big changes with this update, back up your worlds to be safe!g

* Maze worlds can now be created with any world type
  * Instead of selecting the world type "Maze World", a new "Generate Maze" button can be enabled with all presets
  * This should also work with most world generation mods (e.g. Terralith)
  * Instead of extending the existing noise chunk generator, the maze configuration is now stored side by side in the [Dimension definition](https://minecraft.wiki/w/Dimension_definition#JSON_format) in a `maze_generator` field. For more info on the structure, see [here](https://github.com/replaceitem/mazeworld/wiki).
* Added configuration options
  * Min Y and Max Y: Limit the height at which the maze generates
  * Preserve structures: Prevent the end portal room or all structures from being sliced through
  * Replace blocks: Only place maze blocks in air, or all replaceable blocks
  * Dimensions: Select which dimensions will generate with a maze
  * Preserve end island: Keep the end island open without a maze