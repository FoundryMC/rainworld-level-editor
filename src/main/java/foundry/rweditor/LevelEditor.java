package foundry.rweditor;

import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import io.github.ocelot.window.Window;
import io.github.ocelot.window.WindowEventListener;
import io.github.ocelot.window.WindowManager;
import org.lwjgl.opengl.GL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;

public class LevelEditor implements WindowEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LevelEditor.class);

    private final WindowManager windowManager;
    private final Window mainWindow;

    private final ImGuiImplGlfw imGuiGlfw;
    private final ImGuiImplGl3 imGuiGl3;

    public LevelEditor() {
        this.windowManager = new WindowManager();
        this.mainWindow = this.windowManager.create(800, 600, false);
        this.mainWindow.setVsync(true);
        this.mainWindow.addListener(this);

        this.imGuiGlfw = new ImGuiImplGlfw();
        this.imGuiGl3 = new ImGuiImplGl3();
    }

    private void init() throws Throwable {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_API);
        glfwWindowHint(GLFW_CONTEXT_CREATION_API, GLFW_NATIVE_CONTEXT_API);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

        this.mainWindow.create("Editor");
        GL.createCapabilities();
        glfwMakeContextCurrent(this.mainWindow.getHandle());

        ImGui.createContext();
        ImGui.getIO().addConfigFlags(ImGuiConfigFlags.DockingEnable);
        this.imGuiGlfw.init(this.mainWindow.getHandle(), true);
        this.imGuiGl3.init("#version 150 core");
    }

    public void run() {
        try {
            this.init();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        while (!this.mainWindow.isClosed()) {
            this.render();
        }

        this.imGuiGl3.dispose();
        this.imGuiGlfw.dispose();
        ImGui.destroyContext();
        this.windowManager.free();
    }

    private void render() {
        this.windowManager.update();

        glClearColor(0.4F, 0.4F, 0.4F, 1.0F);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        this.imGuiGlfw.newFrame();
        ImGui.newFrame();

        // TODO do render

        ImGui.showDemoWindow();

        ImGui.render();
        this.imGuiGl3.renderDrawData(ImGui.getDrawData());

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            long backupWindowPtr = glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            glfwMakeContextCurrent(backupWindowPtr);
        }
    }

    @Override
    public void framebufferResized(Window window, int width, int height) {
        this.render();
    }

    public static void main(String[] args) {
        new LevelEditor().run();
    }
}
