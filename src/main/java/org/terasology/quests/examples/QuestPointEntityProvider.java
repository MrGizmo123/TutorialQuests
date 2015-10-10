/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.quests.examples;

import org.terasology.asset.Assets;
import org.terasology.assets.management.AssetManager;
import org.terasology.entitySystem.entity.EntityStore;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.logic.location.LocationComponent;
import org.terasology.logic.nameTags.NameTagComponent;
import org.terasology.math.Vector2i;
import org.terasology.math.geom.Vector3f;
import org.terasology.network.NetworkComponent;
import org.terasology.registry.In;
import org.terasology.rendering.nui.Color;
import org.terasology.tasks.components.QuestListComponent;
import org.terasology.world.generation.EntityBuffer;
import org.terasology.world.generation.EntityProvider;
import org.terasology.world.generation.EntityProviderPlugin;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.facets.SurfaceHeightFacet;
import org.terasology.world.generator.plugin.RegisterPlugin;

import com.google.common.collect.Lists;

/**
 * Adds a quest point to any world generator
 */
@RegisterPlugin
public class QuestPointEntityProvider implements EntityProviderPlugin {

    @In
    private AssetManager assetManager;

    @Override
    public void process(Region region, EntityBuffer buffer) {
        SurfaceHeightFacet heightFacet = region.getFacet(SurfaceHeightFacet.class);

        Vector2i location = new Vector2i(16, 16);
        float yOff = 2;

        if (heightFacet.getWorldRegion().contains(location)) {
            float y = heightFacet.getWorld(location);
            if (region.getRegion().minY() <= y && region.getRegion().maxY() >= y) {

                Prefab questPoint = Assets.getPrefab("QuestPoint").get();
                EntityStore entityStore = new EntityStore(questPoint);

                QuestListComponent questList = new QuestListComponent();
                questList.questItems = Lists.newArrayList("QuestCard");
                entityStore.addComponent(questList);

                NameTagComponent nameTagComponent = new NameTagComponent();
                nameTagComponent.text = questList.questItems.toString();
                nameTagComponent.textColor = Color.WHITE;
                nameTagComponent.yOffset = -1;
                nameTagComponent.scale = 2;
                entityStore.addComponent(nameTagComponent);

                Vector3f pos3d = new Vector3f(location.getX(), y + yOff, location.getY());
                LocationComponent locationComponent = questPoint.getComponent(LocationComponent.class);
                if (locationComponent == null) {
                    locationComponent = new LocationComponent(pos3d);
                } else {
                    locationComponent.setWorldPosition(pos3d);
                }
                entityStore.addComponent(locationComponent);

                buffer.enqueue(entityStore);
            }
        }
   }

}