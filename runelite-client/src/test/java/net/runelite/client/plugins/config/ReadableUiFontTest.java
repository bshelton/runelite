/*
 * Copyright (c) 2026, RuneLite contributors
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.config;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.ProgressBar;
import net.runelite.client.ui.laf.RuneLiteLAF;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ReadableUiFontTest
{
	@Test
	public void fontsAndRowsRespectUiSizeWithoutChangingGameFonts() throws Exception
	{
		SwingUtilities.invokeAndWait(() ->
		{
			Font gameFont = FontManager.getRunescapeFont();
			try
			{
				for (int size : new int[]{12, 14, 16, 18, 20})
				{
					assertTrue(RuneLiteLAF.setup(true, size));
					PluginListItem row = row("Agility");
					row.setSize(row.getPreferredSize());
					row.doLayout();
					JLabel label = (JLabel) ((BorderLayout) row.getLayout()).getLayoutComponent(BorderLayout.CENTER);
					assertEquals(size, label.getFont().getSize());
					assertTrue(label.getHeight() >= label.getFontMetrics(label.getFont()).getHeight());
					assertEquals(Font.SANS_SERIF, label.getFont().getFamily());
					assertSame(gameFont, FontManager.getRunescapeFont());
					ProgressBar progress = new ProgressBar();
					progress.setLeftLabel("Progress");
					progress.setSize(225, progress.getPreferredSize().height);
					progress.doLayout();
					for (Component component : progress.getComponents())
					{
						JLabel progressLabel = (JLabel) component;
						assertEquals(Font.SANS_SERIF, progressLabel.getFont().getFamily());
						assertTrue(progressLabel.getHeight() - progressLabel.getInsets().top
							>= progressLabel.getFontMetrics(progressLabel.getFont()).getHeight());
					}
					assertEquals(FontManager.getUiSmallFont(), new HotkeyButton(null, false).getFont());
				}
				RuneLiteLAF.setup(true, 100);
				assertEquals(20, new JLabel().getFont().getSize());
				RuneLiteLAF.setup(true, 1);
				assertEquals(12, new JLabel().getFont().getSize());
				RuneLiteLAF.setup();
				assertEquals(gameFont, UIManager.getFont("defaultFont"));
				assertEquals(20, row("Agility").getPreferredSize().height);
				assertEquals(16, new ProgressBar().getPreferredSize().height);
				assertEquals(FontManager.getDefaultFont().deriveFont(12.f), new HotkeyButton(null, false).getFont());
			}
			finally
			{
				RuneLiteLAF.setup();
			}
		});
	}

	private static PluginListItem row(String name)
	{
		return new PluginListItem(null, new PluginConfigurationDescriptor(name, "Font preview", new String[0], null, null));
	}

	@Test
	public void renderComparison() throws Exception
	{
		SwingUtilities.invokeAndWait(() ->
		{
			try
			{
				File directory = new File("build/font-preview");
				assertTrue(directory.isDirectory() || directory.mkdirs());
				for (double scale : new double[]{1.0, 1.5, 2.0})
				{
					int width = (int) Math.ceil(245 * scale);
					int height = (int) Math.ceil(350 * scale);
					BufferedImage comparison = new BufferedImage(width * 3, height, BufferedImage.TYPE_INT_RGB);
					Graphics2D canvas = comparison.createGraphics();
					for (int column = 0; column < 3; column++)
					{
						RuneLiteLAF.setup(column != 0, column == 2 ? 16 : 14);
						JPanel panel = new JPanel();
						panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
						panel.setBorder(new EmptyBorder(10, 10, 10, 10));
						panel.add(new JLabel(column == 0 ? "Original" : "Readable " + (column == 1 ? 14 : 16)));
						for (String name : new String[]{"117 HD", "Account", "Agility", "Animation Smoothing", "Bank", "Bank Tags", "Barrows Brothers", "Boosts Information", "Chat Commands"})
						{
							PluginListItem row = row(name);
							row.setMaximumSize(new Dimension(225, row.getPreferredSize().height));
							panel.add(row);
						}
						JLabel heading = new JLabel("Window settings");
						heading.setFont(FontManager.getUiBoldFont());
						panel.add(heading);
						panel.add(new JLabel("Remember client position"));
						panel.setSize(245, 350);
						layout(panel);
						Graphics2D graphics = (Graphics2D) canvas.create(column * width, 0, width, height);
						graphics.scale(scale, scale);
						panel.printAll(graphics);
						graphics.dispose();
					}
					canvas.dispose();
					ImageIO.write(comparison, "png", new File(directory, "comparison-" + scale + ".png"));
				}
			}
			catch (Exception ex)
			{
				throw new RuntimeException(ex);
			}
			finally
			{
				RuneLiteLAF.setup();
			}
		});
	}

	private static void layout(Container container)
	{
		container.doLayout();
		for (Component child : container.getComponents())
		{
			if (child instanceof Container)
			{
				layout((Container) child);
			}
		}
	}
}
