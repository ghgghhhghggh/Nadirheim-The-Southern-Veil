package ghgghhhghggh.nadirheim.menu;

import ghgghhhghggh.nadirheim.block.entity.SieveBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/*
 * Fenêtre du tamis (partie commune serveur/client) : les cases et le "shift-clic".
 * Cases 0 = gravier, 1-3 = sorties, 4-30 = inventaire du joueur, 31-39 = barre rapide.
 * L'affichage est dans SieveScreen (côté client).
 */
public class SieveMenu extends AbstractContainerMenu {
	public static final int INPUT_X = 36, OUTPUT_X = 90, SLOTS_Y = 35;
	private static final int PLAYER_START = 4, HOTBAR_START = 31, END = 40;

	private final Container container;
	private final ContainerData data;

	// Côté client : les vraies données arrivent ensuite par le réseau
	public SieveMenu(int containerId, Inventory inventory) {
		this(containerId, inventory, new SimpleContainer(4), new SimpleContainerData(2));
	}

	public SieveMenu(int containerId, Inventory inventory, Container container, ContainerData data) {
		super(ModMenus.SIEVE, containerId);
		checkContainerSize(container, 4);
		checkContainerDataCount(data, 2);
		this.container = container;
		this.data = data;
		container.startOpen(inventory.player);

		addSlot(new Slot(container, SieveBlockEntity.INPUT, INPUT_X, SLOTS_Y) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.is(Items.GRAVEL);
			}
		});
		for (int i = 0; i < 3; i++) {
			addSlot(new Slot(container, 1 + i, OUTPUT_X + 18 * i, SLOTS_Y) {
				@Override
				public boolean mayPlace(ItemStack stack) {
					return false;
				}
			});
		}
		addStandardInventorySlots(inventory, 8, 84);
		addDataSlots(data);
	}

	// Avancement du tamisage entre 0 et 1 (pour la flèche)
	public float getProgress() {
		int total = data.get(1);
		return total == 0 ? 0 : Math.min(1.0F, (float) data.get(0) / total);
	}

	@Override
	public boolean stillValid(Player player) {
		return container.stillValid(player);
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		container.stopOpen(player);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int slotIndex) {
		Slot slot = slots.get(slotIndex);
		if (!slot.hasItem()) {
			return ItemStack.EMPTY;
		}
		ItemStack stack = slot.getItem();
		ItemStack original = stack.copy();

		if (slotIndex < PLAYER_START) {
			// Tamis -> inventaire du joueur
			if (!moveItemStackTo(stack, PLAYER_START, END, true)) {
				return ItemStack.EMPTY;
			}
		} else if (stack.is(Items.GRAVEL)) {
			// Gravier de l'inventaire -> entrée du tamis
			if (!moveItemStackTo(stack, SieveBlockEntity.INPUT, SieveBlockEntity.INPUT + 1, false)) {
				return ItemStack.EMPTY;
			}
		} else if (slotIndex < HOTBAR_START) {
			if (!moveItemStackTo(stack, HOTBAR_START, END, false)) {
				return ItemStack.EMPTY;
			}
		} else if (!moveItemStackTo(stack, PLAYER_START, HOTBAR_START, false)) {
			return ItemStack.EMPTY;
		}

		if (stack.isEmpty()) {
			slot.setByPlayer(ItemStack.EMPTY);
		} else {
			slot.setChanged();
		}
		if (stack.getCount() == original.getCount()) {
			return ItemStack.EMPTY;
		}
		slot.onTake(player, stack);
		return original;
	}
}
