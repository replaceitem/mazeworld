package net.replaceitem.mazeworld.config;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum StructureReplacementType implements StringRepresentable {
    REPLACE_ALL("replace_all"),
    PRESERVE_ESSENTIAL("preserve_essential"),
    PRESERVE_ALL("preserve_all");

    public static final Codec<StructureReplacementType> CODEC = StringRepresentable.fromEnum(StructureReplacementType::values);
    private final String name;
    private final Component displayName;
    private final Component description;

    StructureReplacementType(String name) {
        this.name = name;
        this.displayName = Component.translatable("mazeworld.structure_replacement." + name);
        this.description = Component.translatable("mazeworld.structure_replacement." + name + ".description");
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public Component getDisplayName() {
        return displayName;
    }

    public Component getDescription() {
        return description;
    }
}
