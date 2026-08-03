package net.replaceitem.mazeworld.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.replaceitem.mazeworld.config.StructureReplacementType;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class StructureCondition {
    private final StructureReplacementType structureReplacementType;
    private final WorldGenLevel level;
    private final ChunkPos chunkPos;

    public StructureCondition(StructureReplacementType structureReplacementType, WorldGenLevel level, ChunkPos chunkPos) {
        this.structureReplacementType = structureReplacementType;
        this.level = level;
        this.chunkPos = chunkPos;
    }

    private Predicate<Structure> getStructurePredicate() {
        if(structureReplacementType == StructureReplacementType.PRESERVE_ESSENTIAL) {
            var strongholdStructure = level.registryAccess().lookupOrThrow(Registries.STRUCTURE).getOrThrow(BuiltinStructures.STRONGHOLD).value();
            return structure -> structure == strongholdStructure;
        }
        return _ -> true;
    }

    private Predicate<StructurePiece> getStructurePiecePredicate() {
        if(structureReplacementType == StructureReplacementType.PRESERVE_ESSENTIAL) {
            return structurePiece -> structurePiece.getType() == StructurePieceType.STRONGHOLD_PORTAL_ROOM;
        }
        return _ -> true;
    }

    public Optional<Predicate<BlockPos>> createPredicate() {
        if (structureReplacementType == StructureReplacementType.REPLACE_ALL) return Optional.empty();
        var serverLevel = level.getLevel();

        var structureManager = serverLevel.structureManager();

        List<BoundingBox> holdoutBoundingBoxes = structureManager.startsForStructure(chunkPos, getStructurePredicate())
                .stream().flatMap(structureStart -> structureStart.getPieces().stream())
                .filter(piece -> piece.isCloseToChunk(chunkPos, 0))
                .filter(getStructurePiecePredicate())
                .map(StructurePiece::getBoundingBox)
                .toList();

        if(holdoutBoundingBoxes.isEmpty()) return Optional.empty();

        return Optional.of(pos -> {
            for (BoundingBox holdoutBoundingBox : holdoutBoundingBoxes) {
                if(holdoutBoundingBox.isInside(pos)) return false;
            }
            return true;
        });
    }
}
