package ghgghhhghggh.nadirheim.client.color;

import com.mojang.serialization.MapCodec;
import ghgghhhghggh.nadirheim.Nadirheim;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.ARGB;
import net.minecraft.util.Util;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

/*
 * Teinte "xp" pour les objets (type "nadirheim:xp_orb" dans assets/nadirheim/items/*.json) : la texture, dessinée en
 * gris, pulse vert <-> jaune avec la même formule que les orbes d'xp vanilla (ExperienceOrbRenderer) :
 * vert toujours à fond, rouge qui va et vient (vert + rouge = jaune), une pointe de bleu.
 */
public record XpOrbTint() implements ItemTintSource {
	public static final MapCodec<XpOrbTint> MAP_CODEC = MapCodec.unit(new XpOrbTint());
	private static boolean registered;

	// Appelé par le client ET par la datagen (qui doit connaître le type pour écrire le JSON)
	public static void register() {
		if (!registered) {
			registered = true;
			ItemTintSources.ID_MAPPER.put(Nadirheim.id("xp_orb"), MAP_CODEC);
		}
	}

	@Override
	public int calculate(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
		// Les orbes utilisent (âge en ticks) / 2, soit 0,01 par milliseconde : un cycle complet dure ~0,6 s
		double t = Util.getMillis() / 100.0;
		int red = (int) ((Math.sin(t) + 1.0) * 0.5 * 255.0);
		int blue = (int) ((Math.sin(t + Math.PI * 4.0 / 3.0) + 1.0) * 0.1 * 255.0);
		return ARGB.color(255, red, 255, blue);
	}

	@Override
	public MapCodec<XpOrbTint> type() {
		return MAP_CODEC;
	}
}
