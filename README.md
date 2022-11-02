# MazeWorld

[<img alt="Requires fabric api" height="64" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@2.8.0/assets/cozy/requires/fabric-api_vector.svg">](https://modrinth.com/mod/fabric-api)
[<img alt="Available on Modrinth" height="64" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@2.8.0/assets/cozy/available/modrinth_vector.svg">](https://modrinth.com/mod/discarpet)
[<img alt="Chat on Discord" height="64" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@2.8.0/assets/cozy/social/discord-singular_vector.svg">](https://discord.gg/etTDQAVSgt)

A mod to add an infinite maze out of bedrock to vanilla world generation that is capped off with barrier blocks at the top.

## World creation

To create a mazeworld, select the world type "Maze World".

![World type](https://raw.githubusercontent.com/replaceitem/mazeworld/master/world-type.png)

After clicking on customize, you can change various settings about the maze generation.
You even get a preview of how the maze is going to look like.

![Customization](https://raw.githubusercontent.com/replaceitem/mazeworld/master/customize.png)

## Customization options

### Maze Type

There are different maze types with different algorithms for generating the maze

* [Binary tree](https://weblog.jamisbuck.org/2011/2/1/maze-generation-binary-tree-algorithm)

* [Wang tiles](http://www.cr31.co.uk/stagecast/wang/array.html)

### Spacing

This defines the maze size

### Infinite walls

Prevents players from crossing the walls even above or below build height

## World creation on a server

On a server, you can set the `level-type` in `server.properties` to

```properties
level-type=mazeworld\:maze_world
```

However, that will only create a maze world with default options.
To customize the maze, you have to create the world on the client,
and upload it to the server.