#!/bin/bash
# Lists the real class/method names of the game and Fabric API so the port can use them.
R=/tmp/report
mkdir -p $R/javap
grep -E "error:|warning: \[" $R/build.log | sed 's#/home/runner/work/[^/]*/[^/]*/##' | head -3000 > $R/errors-short.txt
# full error blocks
sed 's#/home/runner/work/[^/]*/[^/]*/##' $R/build.log > $R/build-clean.log

MCJAR=""
while IFS= read -r j; do
  if unzip -l "$j" 2>/dev/null | grep -q "net/minecraft/client/Minecraft.class"; then MCJAR="$j"; fi
done < <(find ~/.gradle /home/runner -name "*.jar" -size +5M 2>/dev/null)
echo "MCJAR=$MCJAR" > $R/jars.txt
find ~/.gradle -name "*.jar" 2>/dev/null | grep -iE "fabric|minecraft" | head -200 >> $R/jars.txt
[ -z "$MCJAR" ] && exit 0
unzip -Z1 "$MCJAR" | grep '\.class$' | sed 's#\.class$##' > $R/classes.txt

WANT="Minecraft GuiGraphicsExtractor Font Screen AbstractWidget AbstractButton Button CycleButton EditBox KeyMapping Camera Entity Options Gui Hud LocalPlayer Player LivingEntity MobEffectInstance MobEffect ItemStack Slot AbstractContainerScreen InventoryScreen CreativeModeInventoryScreen PauseScreen TitleScreen SelectWorldScreen JoinMultiplayerScreen OptionsScreen Window InputConstants Component ChatFormatting Identifier ClientLevel PlayerInfo ClientPacketListener ParticleStatus CameraType FoodData ServerData MouseHandler KeyEvent MouseButtonEvent CharacterEvent AbstractContainerMenu ImageButton SpriteIconButton OptionInstance User GameRenderer EquipmentSlot MobEffects Items Mth"
for n in $WANT; do
  for c in $(grep -E "/$n\$" $R/classes.txt); do
    f=$(echo $c | tr '/' '.')
    javap -protected -cp "$MCJAR" "$f" > "$R/javap/$f.txt" 2>&1
  done
done
# Fabric API classes (names only)
for j in $(find ~/.gradle -name "fabric-*.jar" 2>/dev/null); do
  unzip -Z1 "$j" 2>/dev/null | grep -E '^net/fabricmc/fabric/api/(client/)?(keymapping|keybinding|rendering|event|screen|hud)' | grep '\.class$' | sed "s#^#$(basename $j): #"
done > $R/fabric-classes.txt
for j in $(find ~/.gradle -name "fabric-*.jar" 2>/dev/null); do
  for c in $(unzip -Z1 "$j" 2>/dev/null | grep -E 'net/fabricmc/fabric/api/client/(keymapping|keybinding|rendering/v1/hud)/.*(Helper|Registry|VanillaHud|HudElement)[A-Za-z]*\.class$' | sed 's#\.class$##'); do
    javap -protected -cp "$j" "$(echo $c | tr '/' '.')" > "$R/javap/$(echo $c | tr '/' '.').txt" 2>&1
  done
done
ls $R/javap | wc -l > $R/javap-count.txt
