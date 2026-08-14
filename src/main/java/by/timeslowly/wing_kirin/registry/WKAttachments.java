package by.timeslowly.wing_kirin.registry;

import by.timeslowly.wing_kirin.Wing_kirin;
import com.mojang.serialization.Codec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

// 注册实体 Data Attachment（NeoForge 实体附加数据）
public class WKAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(
            NeoForgeRegistries.ATTACHMENT_TYPES, Wing_kirin.MOD_ID);

    /** 定身「肌肉松弛」标记：被施加超过 50 秒的定身效果时置真，直至本次效果结束（到期/移除/死亡时清除） */
    public static final Supplier<AttachmentType<Boolean>> DING_SHEN_MUSCLE_RELAXED = ATTACHMENTS.register(
            "ding_shen_muscle_relaxed",
            // 26.1 起 AttachmentType.Builder.serialize 需要 MapCodec
            () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL.fieldOf("ding_shen_muscle_relaxed")).build());

    public static void register(IEventBus eventBus) {
        ATTACHMENTS.register(eventBus);
    }
}
