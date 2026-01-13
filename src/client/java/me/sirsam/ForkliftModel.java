package me.sirsam;

import me.sirsam.entity.ForkliftEntity;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class ForkliftModel extends DefaultedEntityGeoModel<@NotNull ForkliftEntity> {
    public ForkliftModel() {
        super(Identifier.fromNamespaceAndPath(Forklift.MOD_ID, "forklift"));
    }
}