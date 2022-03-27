package me.mcblueparrot.client.tweak.transformer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.glassmc.loader.api.GlassLoader;
import com.github.glassmc.loader.api.Listener;
import com.github.glassmc.loader.api.loader.Transformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import me.mcblueparrot.client.tweak.transformer.impl.TransformerGuiButton;
import me.mcblueparrot.client.tweak.transformer.impl.TransformerGuiScreen;
import me.mcblueparrot.client.tweak.transformer.impl.TransformerMinecraft;
import me.mcblueparrot.client.tweak.transformer.impl.TransformerWorldClient;
//import v1_8_9.net.minecraft.launchwrapper.IClassTransformer;

public class ClassTransformer implements Listener, Transformer {

	private List<ClassNodeTransformer> transformers = new ArrayList<>();

	public ClassTransformer() {
		register(new TransformerGuiButton());
		register(new TransformerGuiScreen());
		register(new TransformerWorldClient());
		register(new TransformerMinecraft());
	}

	@Override
	public byte[] transform(String name, byte[] basicClass) {
		List<ClassNodeTransformer> applicable =
				transformers.stream().filter((transformers) -> transformers.test(name.replace(".", "/"))).collect(Collectors.toList());

		ClassReader reader = new ClassReader(basicClass);
		ClassNode clazz = new ClassNode();
		reader.accept(clazz, 0);

		for(ClassNodeTransformer transformer : applicable) {
			try {
				transformer.apply(clazz);
			}
			catch(IOException error) {
				throw new IllegalStateException("Could not transform class " + name, error);
			}
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		clazz.accept(writer);
		return writer.toByteArray();
	}

	public void register(ClassNodeTransformer transformer) {
		transformers.add(transformer);
	}

	@Override
	public void run() {
		GlassLoader.getInstance().registerTransformer(ClassTransformer.class);
	}

	@Override
	public boolean canTransform(String name) {
		return !transformers.stream().filter((transformers) -> transformers.test(name.replace(".", "/"))).collect(Collectors.toList()).isEmpty();
	}
}
