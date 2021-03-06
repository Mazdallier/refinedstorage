package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor;

import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.raoulvdberge.refinedstorage.api.render.IElementDrawers;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CraftingMonitorElementItemRender implements ICraftingMonitorElement {
    public static final String ID = "item_render";

    private int taskId;
    private ItemStack stack;
    private int quantity;
    private int offset;

    public CraftingMonitorElementItemRender(int taskId, ItemStack stack, int quantity, int offset) {
        this.taskId = taskId;
        this.stack = stack;
        this.quantity = quantity;
        this.offset = offset;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void draw(int x, int y, IElementDrawers drawers) {
        drawers.getItemDrawer().draw(x + 2 + offset, y + 1, stack);

        float scale = 0.5f;

        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, 1);

        drawers.getStringDrawer().draw(RenderUtils.getOffsetOnScale(x + 21 + offset, scale), RenderUtils.getOffsetOnScale(y + 7, scale), quantity + "x " + stack.getDisplayName());

        GlStateManager.popMatrix();
    }

    @Override
    public boolean canDrawSelection() {
        return true;
    }

    @Override
    public int getTaskId() {
        return taskId;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeInt(taskId);
        ByteBufUtils.writeItemStack(buf, stack);
        buf.writeInt(quantity);
        buf.writeInt(offset);
    }

    @Override
    public boolean merge(ICraftingMonitorElement element) {
        if (element.getId().equals(getId()) && elementHashCode() == element.elementHashCode()) {
            this.quantity += ((CraftingMonitorElementItemRender) element).quantity;

            return true;
        }

        return false;
    }

    @Override
    public int elementHashCode() {
        return API.instance().getItemStackHashCode(stack);
    }
}
