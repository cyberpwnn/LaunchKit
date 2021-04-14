package ninja.bytecode.shuriken.tools;

import java.awt.Graphics;

import javax.swing.JFrame;

public abstract class Scratchpad
{
	private JFrame f;

	public Scratchpad()
	{
		f = new JFrame("Scratchpad");
		f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		f.add(new ScratchpadPanel()
		{
			private static final long serialVersionUID = 7932047221974269087L;

			@Override
			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				Scratchpad.this.paint(g, Scratchpad.this);
			}
		});
		f.pack();
		f.setSize(1000, 1000);
		f.setVisible(true);
	}

	public JFrame getFrame()
	{
		return f;
	}

	public int getWidth()
	{
		return f.getWidth();
	}

	public int getHeight()
	{
		return f.getHeight();
	}

	public void redraw()
	{
		f.repaint();
	}

	public void hide()
	{
		f.setVisible(false);
	}

	public abstract void paint(Graphics g, Scratchpad pad);
}
