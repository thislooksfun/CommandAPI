package com.tlf.commandapi.ASM;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;

@MCVersion(value = "1.7.2")
public class TransformCore implements IFMLLoadingPlugin
{
	@Override
	public String[] getASMTransformerClass() {
		return new String[]{Transformer.class.getName()};
	}

	@Override
	public String getModContainerClass() { return null; }

	@Override
	public String getSetupClass() { return null; }

	@Override
	public void injectData(Map<String, Object> data) {}

	@Override
	public String getAccessTransformerClass() { return null; }
}
