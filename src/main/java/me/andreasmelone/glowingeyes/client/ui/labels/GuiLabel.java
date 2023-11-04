package me.andreasmelone.glowingeyes.client.ui.labels;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiLabel extends Gui
{
    protected int width = 200;
    protected int height = 20;
    public int x;
    public int y;
    private final List<String> labels;
    public int id;
    private boolean centered;
    public boolean visible = true;
    private boolean labelBgEnabled;
    private final int textColor;
    private int backColor;
    private int ulColor;
    private int brColor;
    private final FontRenderer fontRenderer;
    private int border;

    /**
     * Create a new GuiLabel
     * @param fontRendererObj The font renderer to use
     * @param id The component ID of the label
     * @param x The x position of the label
     * @param y The y position of the label
     * @param width The width of the label
     * @param height The height of the label
     * @param textColor The color of the text
     */
    public GuiLabel(FontRenderer fontRendererObj, int id, int x, int y, int width, int height, int textColor)
    {
        this.fontRenderer = fontRendererObj;
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.labels = Lists.<String>newArrayList();
        this.centered = false;
        this.labelBgEnabled = false;
        this.textColor = textColor;
        this.backColor = -1;
        this.ulColor = -1;
        this.brColor = -1;
        this.border = 0;
    }

    public void addLine(String p_175202_1_)
    {
        this.labels.add(I18n.format(p_175202_1_));
    }

    public GuiLabel setCentered()
    {
        this.centered = true;
        return this;
    }

    public void drawLabel(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            this.drawLabelBackground(mc, mouseX, mouseY);
            int i = this.y + this.height / 2 + this.border / 2;
            int j = i - this.labels.size() * 10 / 2;

            for (int k = 0; k < this.labels.size(); ++k)
            {
                if (this.centered)
                {
                    this.drawCenteredString(this.fontRenderer, this.labels.get(k), this.x + this.width / 2, j + k * 10, this.textColor);
                }
                else
                {
                    this.drawString(this.fontRenderer, this.labels.get(k), this.x, j + k * 10, this.textColor);
                }
            }
        }
    }

    protected void drawLabelBackground(Minecraft mcIn, int p_146160_2_, int p_146160_3_)
    {
        if (this.labelBgEnabled)
        {
            int i = this.width + this.border * 2;
            int j = this.height + this.border * 2;
            int k = this.x - this.border;
            int l = this.y - this.border;
            drawRect(k, l, k + i, l + j, this.backColor);
            this.drawHorizontalLine(k, k + i, l, this.ulColor);
            this.drawHorizontalLine(k, k + i, l + j, this.brColor);
            this.drawVerticalLine(k, l, l + j, this.ulColor);
            this.drawVerticalLine(k + i, l, l + j, this.brColor);
        }
    }
}
