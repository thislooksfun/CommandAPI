package com.tlf.commandapi.ASM;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.LDC;

import net.minecraft.launchwrapper.IClassTransformer;

import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class Transformer implements IClassTransformer {

	@Override
	public byte[] transform(String arg0, String arg1, byte[] arg2)
	{
		
		if (arg0.equals("jv" /*null*/)) {
			return patchClassASM(arg0, arg2, true);
		} else if (arg0.equals("net.minecraft.entity.player.EntityPlayerMP" /*null*/)) {
			return patchClassASM(arg0, arg2, false);
		}

		return arg2;
	}

	public byte[] patchClassASM(String name, byte[] bytes, boolean obfuscated)
	{
		System.out.println("Patching!");
		String targetMethodName = obfuscated ? "a" : "canCommandSenderUseCommand"; //null
		String targetMethodDesc = "(ILjava/lang/String;)Z";

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);

		Iterator<MethodNode> methods = classNode.methods.iterator();
		while(methods.hasNext())
		{
			MethodNode m = methods.next();
			int tell_index = -1;

			if ((m.name.equals(targetMethodName) && m.desc.equals(targetMethodDesc)))
			{
				AbstractInsnNode currentNode = null;
				AbstractInsnNode targetNode = null;
				
				Iterator<AbstractInsnNode> iter = m.instructions.iterator();

				int index = -1;

				while (iter.hasNext())
				{
					index++;
					currentNode = iter.next();
					
					if (currentNode.getOpcode() == LDC) {
						LdcInsnNode temp = (LdcInsnNode)currentNode;
						
						if (temp.cst.equals("tell")) {
							tell_index = index;
						} else if (temp.cst.equals("me")) {
							targetNode = currentNode.getNext();
						}
					}
				}
				
				
				
				if (targetNode == null || tell_index == -1)
                {
                    return bytes;
                }
                
				AbstractInsnNode[] remNodes = new AbstractInsnNode[10];
				remNodes[0] = m.instructions.get(tell_index);
				remNodes[1] = m.instructions.get(tell_index+1);
				remNodes[2] = m.instructions.get(tell_index+2);
				remNodes[3] = m.instructions.get(tell_index+3);
				remNodes[4] = m.instructions.get(tell_index+4);
				remNodes[5] = m.instructions.get(tell_index+5);
				remNodes[6] = m.instructions.get(tell_index+6);
				remNodes[7] = m.instructions.get(tell_index+7);
				remNodes[8] = m.instructions.get(tell_index+8);
				remNodes[9] = m.instructions.get(tell_index+10);

				for (int i = 0; i < remNodes.length; i++)
				{
					m.instructions.remove(remNodes[i]);
				}
				
				InsnList toInject = new InsnList();
				toInject.add(new MethodInsnNode(INVOKESTATIC, "tlf/commandapi/common/CommandAPI", "isPlayerCommand", "(Ljava/lang/String;)Z"));
				m.instructions.insert(targetNode, toInject);
				break;
			}
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		return writer.toByteArray();
	}
}