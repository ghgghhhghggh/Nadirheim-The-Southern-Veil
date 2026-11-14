package ghgghhhghggh.nadirheim.block.entity;

import ghgghhhghggh.nadirheim.menu.SieveMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

/*
 * Tamis : transforme le gravier, un par un, en sable (45 %), roche (45 %) ou silex (10 %).
 * Fonctionne seul, sans combustible (SIFT_TIME ticks par gravier), comme un four.
 * Cases : 0 = gravier (entrée), 1 à 3 = sorties.
 * Entonnoirs : on remplit par le haut ou les côtés, on vide par le dessous.
 */
public class SieveBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
	public static final int INPUT = 0;
	public static final int SIFT_TIME = 80;   // 4 secondes par gravier
	private static final int[] OUTPUTS = {1, 2, 3};
	private static final int[] INPUT_SLOTS = {INPUT};
	private static final Item[] POSSIBLE_RESULTS = {Items.SAND, Items.COBBLESTONE, Items.FLINT};

	private NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
	private int progress;

	// Progression envoyée à la fenêtre du joueur (pour la flèche)
	private final ContainerData data = new ContainerData() {
		@Override
		public int get(int index) {
			return index == 0 ? progress : SIFT_TIME;
		}

		@Override
		public void set(int index, int value) {
			if (index == 0) {
				progress = value;
			}
		}

		@Override
		public int getCount() {
			return 2;
		}
	};

	public SieveBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.SIEVE, pos, state);
	}

	public static void serverTick(ServerLevel level, BlockPos pos, BlockState state, SieveBlockEntity sieve) {
		ItemStack input = sieve.items.get(INPUT);
		if (input.is(Items.GRAVEL) && sieve.hasRoomForAnyResult()) {
			sieve.progress++;
			if (sieve.progress % 20 == 1) {
				level.playSound(null, pos, SoundEvents.BRUSH_GRAVEL, SoundSource.BLOCKS, 0.6F, 1.0F);
			}
			if (sieve.progress >= SIFT_TIME) {
				sieve.progress = 0;
				input.shrink(1);
				sieve.insertResult(new ItemStack(rollResult(level.getRandom())));
			}
			setChanged(level, pos, state);
		} else if (sieve.progress != 0) {
			sieve.progress = 0;
			setChanged(level, pos, state);
		}
	}

	private static Item rollResult(RandomSource random) {
		int roll = random.nextInt(100);
		if (roll < 45) {
			return Items.SAND;
		}
		return roll < 90 ? Items.COBBLESTONE : Items.FLINT;
	}

	// On ne tamise que si n'importe lequel des 3 résultats possibles a sa place dans les sorties
	private boolean hasRoomForAnyResult() {
		for (Item result : POSSIBLE_RESULTS) {
			if (!canInsert(result)) {
				return false;
			}
		}
		return true;
	}

	private boolean canInsert(Item result) {
		for (int slot : OUTPUTS) {
			ItemStack stack = items.get(slot);
			if (stack.isEmpty() || (stack.is(result) && stack.getCount() < stack.getMaxStackSize())) {
				return true;
			}
		}
		return false;
	}

	private void insertResult(ItemStack result) {
		// D'abord compléter une pile du même objet, sinon prendre une case vide
		for (int slot : OUTPUTS) {
			ItemStack stack = items.get(slot);
			if (ItemStack.isSameItemSameComponents(stack, result) && stack.getCount() < stack.getMaxStackSize()) {
				stack.grow(1);
				return;
			}
		}
		for (int slot : OUTPUTS) {
			if (items.get(slot).isEmpty()) {
				items.set(slot, result);
				return;
			}
		}
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable("container.nadirheim.sieve");
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return items;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> items) {
		this.items = items;
	}

	@Override
	public int getContainerSize() {
		return items.size();
	}

	@Override
	protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
		return new SieveMenu(containerId, inventory, this, data);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(input, items);
		progress = input.getIntOr("sift_progress", 0);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		ContainerHelper.saveAllItems(output, items);
		output.putInt("sift_progress", progress);
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return slot == INPUT && stack.is(Items.GRAVEL);
	}

	@Override
	public int[] getSlotsForFace(Direction direction) {
		return direction == Direction.DOWN ? OUTPUTS : INPUT_SLOTS;
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction direction) {
		return canPlaceItem(slot, stack);
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
		return slot != INPUT;
	}
}
