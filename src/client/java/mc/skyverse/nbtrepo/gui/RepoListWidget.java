package mc.skyverse.nbtrepo.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.mojang.blaze3d.systems.RenderSystem;

import mc.skyverse.nbtrepo.gui.screen.RepoScreen;
import mc.skyverse.nbtrepo.web.Request;
import mc.skyverse.nbtrepo.web.Request.Method;
import mc.skyverse.nbtrepo.web.Server;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.LoadingDisplay;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.input.KeyCodes;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class RepoListWidget extends AlwaysSelectedEntryListWidget<RepoListWidget.Entry> {

	static final Identifier JOIN_HIGHLIGHTED_TEXTURE = new Identifier("world_list/join_highlighted");

	static final Text EXPERIMENTAL_TEXT = (Text)Text.translatable("selectWorld.experimental");

	private final RepoScreen parent;

	private CompletableFuture<List<RepoSummary>> reposFuture;

	@Nullable
	private List<RepoSummary> repos;

	private RepoSummary repo;

	private String search = "";
	
	private String ip = "";
	
	private Server server;

	private long time;

	private final mc.skyverse.nbtrepo.gui.RepoListWidget.RepoEntry.LoadingEntry loadingEntry;

	public RepoListWidget(RepoScreen parent, MinecraftClient client, int width, int height, int y, int itemHeight, @Nullable RepoListWidget oldWidget) {

		super(client, width, height, y, itemHeight);
		this.setX(parent.width / 2 - width / 2);
		this.parent = parent;
		this.loadingEntry = new mc.skyverse.nbtrepo.gui.RepoListWidget.RepoEntry.LoadingEntry(client);	

		this.reposFuture = oldWidget != null ? oldWidget.reposFuture : loadNBTs();

		show(tryGet());
	}

	protected void clearEntries() {

		children().forEach(Entry::close);
		super.clearEntries();
	}

	@Nullable
	private List<RepoSummary> tryGet() {
		try {
			return this.reposFuture.getNow(null);
		} catch (CompletionException|java.util.concurrent.CancellationException runtimeException) {
			return null;
		} 
	}

	void load() {
		this.reposFuture = loadNBTs();
	}

	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

		if (KeyCodes.isToggle(keyCode)) {

			Optional<RepoEntry> optional = getSelectedAsOptional();
			if (optional.isPresent()) {

				this.client.getSoundManager().play((SoundInstance)PositionedSoundInstance.master((RegistryEntry)SoundEvents.UI_BUTTON_CLICK, 1.0F));
				//					((RepoEntry)optional.get()).play();
				return true;
			} 
		} 
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {

		List<RepoSummary> list = tryGet();
		if (list != this.repos) show(list);

		super.renderWidget(context, mouseX, mouseY, delta);
	}

	private void show(@Nullable List<RepoSummary> repos) {

		if (repos == null) {

			showLoadingScreen();

		} else {

			showSummaries(this.search, repos);
		} 
		this.repos = repos;
	}

	public void setSearch(String search) {

		if (this.repos != null && !search.equals(this.search)) showSummaries(search, this.repos); 

		this.search = search;
		this.reposFuture = loadNBTs();
	}
	
	public void setServer(String ip) {

		this.ip = ip;
		this.server = new Server(ip);
	}

	private CompletableFuture<List<RepoSummary>> loadNBTs() {

		List<RepoSummary> entries = new ArrayList<RepoSummary>();
		
		if (this.ip.length() > 2) {
			
			Request get = Request.prefill(Method.GET, "/nbt");
			
			Object[] o = get.send(this.server);
			
//			if ((int)o[0] != 200) { System.err.println(o[1]); return CompletableFuture.completedFuture(entries);};
			
			RepoSummary[] nbts = new Gson().fromJson((String)o[2], RepoSummary[].class);
			
			for (RepoSummary nbt : nbts) {
				
				entries.add(nbt);
			}
		}
		
//		for (int i = 0; i < 3; i++) {
//
//			entries.add(new RepoSummary("NBT #" + i, "sky", 0, ""));
//		}

		//		List.of()
		return CompletableFuture.completedFuture(entries);



		//		LevelStorage.LevelList levelList;
		//		try {
		//			levelList = this.client.getLevelStorage().getLevelList();
		//		} catch (LevelStorageException levelStorageException) {
		////			LOGGER.error("Couldn't load level list", (Throwable)levelStorageException);
		//			showUnableToLoadScreen(levelStorageException.getMessageText());
		//			return CompletableFuture.completedFuture(List.of());
		//		} 
		//		if (levelList.isEmpty()) {
		//			CreateWorldScreen.create(this.client, null);
		//			return CompletableFuture.completedFuture(List.of());
		//		} 
		//		return this.client.getLevelStorage().loadSummaries(levelList)
		//				.exceptionally(throwable -> {
		//					this.client.setCrashReportSupplierAndAddDetails(CrashReport.create(throwable, "Couldn't load level list"));
		//					return List.of();
		//				});
		//		return null;
	}

	private void showSummaries(String search, List<RepoSummary> summaries) {

		clearEntries();
		search = search.toLowerCase(Locale.ROOT);
		for (RepoSummary summary : summaries) {
			if (shouldShow(search, summary))
				addEntry((RepoListWidget.Entry)new RepoEntry(this, this, summary)); 
		}
	}

	private boolean shouldShow(String search, RepoSummary summary) {

		return (summary.getName().toLowerCase(Locale.ROOT).contains(search) || summary.getName().toLowerCase(Locale.ROOT).contains(search));
	}

	private void showLoadingScreen() {

		clearEntries();
		
		//		addEntry((EntryListWidget.Entry)this.loadingEntry);
	}

	//	private void narrateScreenIfNarrationEnabled() {
	//		setScrollAmount(getScrollAmount());
	//		this.parent.narrateScreenIfNarrationEnabled(true);
	//	}
	////
	//	private void showUnableToLoadScreen(Text message) {
	//		this.client.setScreen((Screen)new FatalErrorScreen((Text)Text.translatable("selectWorld.unable_to_load"), message));
	//	}

	protected int getScrollbarPositionX() {

		return super.getScrollbarPositionX() + 20;
	}

	public int getRowWidth() {

		return super.getRowWidth() + 50;
	}

	public void setSelected(@Nullable Entry entry) {
		//		super.setSelected((EntryListWidget.Entry)entry);
		RepoEntry repoEntry = (RepoEntry)entry;
		//		this.parent.worldSelected((entry instanceof RepoEntry) ? repoEntry.level : null);
	}

	public Optional<RepoEntry> getSelectedAsOptional() {

		Entry entry = (Entry)getSelectedOrNull();

		if (entry instanceof RepoEntry) {

			return Optional.of((RepoEntry)entry);
		} 
		return Optional.empty();
	}

	public RepoScreen getParent() {

		return this.parent;
	}

	//	public void appendClickableNarrations(NarrationMessageBuilder builder) {
	//		
	//		if (children().contains(this.loadingEntry)) {
	//		
	//			this.loadingEntry.appendNarrations(builder);
	//			return;
	//		} 
	//		super.appendClickableNarrations(builder);
	//	}

	@Environment(EnvType.CLIENT)
	public static abstract class Entry extends AlwaysSelectedEntryListWidget.Entry<Entry> implements AutoCloseable {
		public void close() {}
	}


	public boolean mouseClicked(double mouseX, double mouseY, int button) {

		System.out.println("hello");
//		
//		if (true) return false;
		//		if (!this.repo.isSelectable()) return true;

		//		this.field_19135.setSelected(this);
		if (mouseX /*- this.entry.getRowLeft()*/ <= 32.0D || Util.getMeasuringTimeMs() - this.time < 250L) {
			if (true) {
				this.client.getSoundManager().play((SoundInstance)PositionedSoundInstance.master((RegistryEntry)SoundEvents.UI_BUTTON_CLICK, 1.0F));
				display();
			}
			return true;
		} 
		this.time = Util.getMeasuringTimeMs();
		return true;
	}

	public void display() {

		
		//		this.client.setScreen(this.screen);
	}

	//		private void loadIcon() {
	//			boolean bl = (this.iconPath != null && Files.isRegularFile(this.iconPath, new LinkOption[0]));
	//			if (bl) {
	//				try {
	//					InputStream inputStream = Files.newInputStream(this.iconPath, new java.nio.file.OpenOption[0]);
	//					try {
	//						this.icon.load(NativeImage.read(inputStream));
	//						if (inputStream != null)
	//							inputStream.close(); 
	//					} catch (Throwable throwable) {
	//						if (inputStream != null)
	//							try {
	//								inputStream.close();
	//							} catch (Throwable throwable1) {
	//								throwable.addSuppressed(throwable1);
	//							}  
	//						throw throwable;
	//					} 
	//				} catch (Throwable throwable) {
	//					WorldListWidget.LOGGER.error("Invalid icon for world {}", this.level.getName(), throwable);
	//					this.iconPath = null;
	//				} 
	//			} else {
	//				this.icon.destroy();
	//			} 
	//		}

	public String getRepoName() {

		return this.repo.getName();
	}


	@Environment(EnvType.CLIENT)
	public final class RepoEntry extends Entry implements AutoCloseable {

		private static final int field_32435 = 32;

		private static final int field_32436 = 32;

		private final MinecraftClient client;

		private final RepoScreen screen;

		private RepoSummary repo;

		private long time;

		public RepoEntry(RepoListWidget worldListWidget, RepoListWidget levelList, RepoSummary repo) {

			this.client = MinecraftClient.getInstance();
			//			this.client = WorldListWidget.method_43452(levelList);
			this.screen = levelList.getParent();
			this.repo = repo;
			//			this.icon = WorldIcon.forWorld(this.client.getTextureManager(), level.getName());
			//			this.iconPath = level.getIconPath();
			//			validateIconPath();
			//			loadIcon();
		}

		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {

			//			if (StringUtils.isEmpty(string))
			//				string = I18n.translate("selectWorld.world", new Object[0]) + " " + I18n.translate("selectWorld.world", new Object[0]); 
			//			Text text = this.level.getDetails();
			context.drawText(this.client.textRenderer, "§l" + this.repo.getName(), x + 32 + 3, y + 1, 16777215, false);
			Objects.requireNonNull(this.client.textRenderer);
			context.drawText(this.client.textRenderer, "By " + this.repo.getAuthor(), x + 32 + 3, y + 9 + 3, -8355712, false);
			Objects.requireNonNull(this.client.textRenderer);
			Objects.requireNonNull(this.client.textRenderer);
			context.drawText(this.client.textRenderer, this.repo.getDownloadsCount() + " downloads.", x + 32 + 3, y + 9 + 9 + 3, -8355712, false);
			RenderSystem.enableBlend();
			//			context.drawTexture(this.icon.getTextureId(), x, y, 0.0F, 0.0F, 32, 32, 32, 32);
			RenderSystem.disableBlend();
			//			if (((Boolean)this.client.options.getTouchscreen().getValue()).booleanValue() || hovered) {
			//				context.fill(x, y, x + 32, y + 32, -1601138544);
			//				int i = mouseX - x;
			//				boolean bl = (i < 32);
			//				Identifier identifier = bl ? WorldListWidget.JOIN_HIGHLIGHTED_TEXTURE : WorldListWidget.JOIN_TEXTURE;
			//				Identifier identifier2 = bl ? WorldListWidget.WARNING_HIGHLIGHTED_TEXTURE : WorldListWidget.WARNING_TEXTURE;
			//				Identifier identifier3 = bl ? WorldListWidget.ERROR_HIGHLIGHTED_TEXTURE : WorldListWidget.ERROR_TEXTURE;
			//				Identifier identifier4 = bl ? WorldListWidget.MARKED_JOIN_HIGHLIGHTED_TEXTURE : WorldListWidget.MARKED_JOIN_TEXTURE;
			//				if (this.repo instanceof ReSummary.SymlinkLevelSummary || this.level instanceof LevelSummary.RecoveryWarning) {
			//					context.drawGuiTexture(identifier3, x, y, 32, 32);
			//					context.drawGuiTexture(identifier4, x, y, 32, 32);}
			//					return;
			//				} 
			//				if (this.level.isLocked()) {
			//					context.drawGuiTexture(identifier3, x, y, 32, 32);
			//					if (bl)
			//						this.screen.setTooltip(this.client.textRenderer.wrapLines((StringVisitable)WorldListWidget.LOCKED_TEXT, 175)); 
			//				} else if (this.level.requiresConversion()) {
			//					context.drawGuiTexture(identifier3, x, y, 32, 32);
			//					if (bl)
			//						this.screen.setTooltip(this.client.textRenderer.wrapLines((StringVisitable)WorldListWidget.CONVERSION_TOOLTIP, 175)); 
			//				} else if (!this.level.isVersionAvailable()) {
			//					context.drawGuiTexture(identifier3, x, y, 32, 32);
			//					if (bl)
			//						this.screen.setTooltip(this.client.textRenderer.wrapLines((StringVisitable)WorldListWidget.INCOMPATIBLE_TOOLTIP, 175)); 
			//				} else if (this.level.shouldPromptBackup()) {
			//					context.drawGuiTexture(identifier4, x, y, 32, 32);
			//					if (this.level.wouldBeDowngraded()) {
			//						context.drawGuiTexture(identifier3, x, y, 32, 32);
			//						if (bl)
			//							this.screen.setTooltip((List)ImmutableList.of(WorldListWidget.FROM_NEWER_VERSION_FIRST_LINE.asOrderedText(), WorldListWidget.FROM_NEWER_VERSION_SECOND_LINE.asOrderedText())); 
			//					} else if (!SharedConstants.getGameVersion().isStable()) {
			//						context.drawGuiTexture(identifier2, x, y, 32, 32);
			//						if (bl)
			//							this.screen.setTooltip((List)ImmutableList.of(WorldListWidget.SNAPSHOT_FIRST_LINE.asOrderedText(), WorldListWidget.SNAPSHOT_SECOND_LINE.asOrderedText())); 
			//					} 
			//				} else {
			//					context.drawGuiTexture(identifier, x, y, 32, 32);
		}

		@Override
		public Text getNarration() {

			return Text.literal("");
		}

		@Environment(EnvType.CLIENT)
		public static class LoadingEntry extends Entry {

			private static final Text LOADING_LIST_TEXT = (Text)Text.literal("Loading...");

			private final MinecraftClient client;

			public LoadingEntry(MinecraftClient client) {

				this.client = client;
			}

			public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {

				int i = (this.client.currentScreen.width - this.client.textRenderer.getWidth((StringVisitable)LOADING_LIST_TEXT)) / 2;
				Objects.requireNonNull(this.client.textRenderer);
				int j = y + (entryHeight - 9) / 2;
				context.drawText(this.client.textRenderer, LOADING_LIST_TEXT, i, j, 16777215, false);
				String string = LoadingDisplay.get(Util.getMeasuringTimeMs());
				int k = (this.client.currentScreen.width - this.client.textRenderer.getWidth(string)) / 2;
				Objects.requireNonNull(this.client.textRenderer);
				int l = j + 9;
				context.drawText(this.client.textRenderer, string, k, l, -8355712, false);
			}

			@Override
			public Text getNarration() {
				return Text.literal("");
			}
		}
	}

}