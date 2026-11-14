package ghgghhhghggh.nadirheim.menu;

import ghgghhhghggh.nadirheim.Nadirheim;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

// Types de fenêtres (menus) du mod ; leur affichage est enregistré côté client dans NadirheimClient
public class ModMenus {
	public static final MenuType<SieveMenu> SIEVE = Registry.register(BuiltInRegistries.MENU, Nadirheim.id("sieve"),
			new MenuType<>(SieveMenu::new, FeatureFlags.VANILLA_SET));

	public static void initialize() {
		// Charge la classe (et donc enregistre les menus ci-dessus)
	}
}
