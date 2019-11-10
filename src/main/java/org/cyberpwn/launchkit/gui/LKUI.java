package org.cyberpwn.launchkit.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedInputStream;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.cyberpwn.launchkit.LaunchKit;

import ninja.bytecode.shuriken.execution.J;

public class LKUI extends JFrame
{
	private JPanel contentPane;
	private boolean maxed = false;
	private JTextField login_user;
	private JPasswordField login_pass;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		J.attempt(() -> UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()));

		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					LKUI frame = new LKUI();
					frame.setVisible(true);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public LKUI()
	{
		setResizable(false);
		setUndecorated(true);
		setTitle("Launch Kit");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 400, 550);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		setContentPane(contentPane);
		MoveMouseListener mml = new MoveMouseListener(contentPane);
		addMouseListener(mml);
		addMouseMotionListener(mml);

		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0), 0));

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(new Color(0, 0, 0), 0));

		JLabel minimizeButton = new JLabel("");
		panel.add(minimizeButton);
		minimizeButton.setIcon(new ImageIcon(LKUI.class.getResource("/org/cyberpwn/launchkit/gui/minus.png")));

		JLabel closeButton = new JLabel("");
		panel.add(closeButton);
		closeButton.setIcon(new ImageIcon(LKUI.class.getResource("/org/cyberpwn/launchkit/gui/close.png")));

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new LineBorder(new Color(0, 0, 0), 0));

		JPanel login_pane = new JPanel();
		login_pane.setBorder(new LineBorder(new Color(0, 0, 0), 0));

		JProgressBar progressBar = new JProgressBar();
		progressBar.setForeground(new Color(0, 0, 0));
		progressBar.setValue(33);
		progressBar.setBorder(new LineBorder(new Color(0, 0, 0), 0));

		JPanel doit = new JPanel();
		doit.setBorder(new LineBorder(new Color(0, 0, 0), 0));
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addGroup(gl_contentPane.createSequentialGroup().addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE).addGroup(gl_contentPane.createSequentialGroup().addContainerGap(306, Short.MAX_VALUE).addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addComponent(panel_2, GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE).addGroup(gl_contentPane.createSequentialGroup().addGap(42).addComponent(login_pane, GroupLayout.PREFERRED_SIZE, 316, GroupLayout.PREFERRED_SIZE)).addComponent(doit, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE).addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 396, Short.MAX_VALUE)).addContainerGap()));
		gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addGroup(gl_contentPane.createSequentialGroup().addComponent(panel, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED).addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE).addGap(11).addComponent(login_pane, GroupLayout.PREFERRED_SIZE, 223, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED).addComponent(doit, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED, 19, Short.MAX_VALUE).addComponent(panel_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED).addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 4, GroupLayout.PREFERRED_SIZE)));

		JLabel stopit = new JLabel("");
		doit.add(stopit);
		stopit.setIcon(new ImageIcon(LKUI.class.getResource("/org/cyberpwn/launchkit/gui/stop.png")));

		JLabel play = new JLabel("");
		doit.add(play);
		play.setIcon(new ImageIcon(LKUI.class.getResource("/org/cyberpwn/launchkit/gui/play.png")));

		login_user = new JTextField();
		login_user.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 18));
		login_user.setHorizontalAlignment(SwingConstants.CENTER);
		login_user.setText("");
		login_user.setColumns(10);

		login_pass = new JPasswordField();
		login_pass.setHorizontalAlignment(SwingConstants.CENTER);
		login_pass.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 18));
		login_pass.setText("");

		JLabel label_6 = new JLabel("");
		label_6.setIcon(new ImageIcon(LKUI.class.getResource("/org/cyberpwn/launchkit/gui/lock.png")));

		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new LineBorder(new Color(0, 0, 0), 0));
		GroupLayout gl_login_pane = new GroupLayout(login_pane);
		gl_login_pane.setHorizontalGroup(gl_login_pane.createParallelGroup(Alignment.LEADING).addGroup(gl_login_pane.createSequentialGroup().addContainerGap().addGroup(gl_login_pane.createParallelGroup(Alignment.LEADING).addComponent(login_pass, GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE).addComponent(login_user, GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)).addContainerGap()).addGroup(gl_login_pane.createSequentialGroup().addContainerGap(134, Short.MAX_VALUE).addComponent(label_6, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE).addGap(132)).addGroup(gl_login_pane.createSequentialGroup().addGap(99).addComponent(panel_4, GroupLayout.PREFERRED_SIZE, 118, GroupLayout.PREFERRED_SIZE).addContainerGap(99, Short.MAX_VALUE)));
		gl_login_pane.setVerticalGroup(gl_login_pane.createParallelGroup(Alignment.TRAILING).addGroup(gl_login_pane.createSequentialGroup().addComponent(label_6, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(login_user, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED).addComponent(login_pass, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addGap(28).addComponent(panel_4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addGap(106)));

		JLabel login_cancel = new JLabel("");
		panel_4.add(login_cancel);
		login_cancel.setIcon(new ImageIcon(LKUI.class.getResource("/org/cyberpwn/launchkit/gui/cancel.png")));

		JLabel login_ok = new JLabel("");
		panel_4.add(login_ok);
		login_ok.setIcon(new ImageIcon(LKUI.class.getResource("/org/cyberpwn/launchkit/gui/ok.png")));
		login_pane.setLayout(gl_login_pane);

		JLabel icon_disconnected = new JLabel("");
		panel_2.add(icon_disconnected);
		icon_disconnected.setIcon(new ImageIcon(LKUI.class.getResource("/org/cyberpwn/launchkit/gui/disconnected.png")));

		JLabel icon_connected = new JLabel("");
		panel_2.add(icon_connected);
		icon_connected.setIcon(new ImageIcon(LKUI.class.getResource("/org/cyberpwn/launchkit/gui/connected.png")));

		JLabel icon_syncing = new JLabel("");
		panel_2.add(icon_syncing);
		icon_syncing.setIcon(new ImageIcon(LKUI.class.getResource("/org/cyberpwn/launchkit/gui/cloud_sync.png")));

		JLabel icon_synced = new JLabel("");
		panel_2.add(icon_synced);
		icon_synced.setIcon(new ImageIcon(LKUI.class.getResource("/org/cyberpwn/launchkit/gui/cloud_ok.png")));

		JLabel packname = new JLabel("Pack Name");
		packname.setHorizontalAlignment(SwingConstants.CENTER);
		packname.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 38));

		JLabel packv = new JLabel("1.43");
		packv.setForeground(Color.GRAY);
		packv.setBackground(Color.GRAY);
		packv.setHorizontalAlignment(SwingConstants.CENTER);
		packv.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 18));
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(gl_panel_1.createParallelGroup(Alignment.LEADING).addGroup(gl_panel_1.createSequentialGroup().addContainerGap().addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING).addComponent(packv, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE).addComponent(packname, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)).addContainerGap()));
		gl_panel_1.setVerticalGroup(gl_panel_1.createParallelGroup(Alignment.LEADING).addGroup(gl_panel_1.createSequentialGroup().addComponent(packname).addPreferredGap(ComponentPlacement.RELATED).addComponent(packv).addContainerGap(14, Short.MAX_VALUE)));
		panel_1.setLayout(gl_panel_1);
		contentPane.setLayout(gl_contentPane);

		// INIT STUFF
		progressBar.setIndeterminate(true);
		LaunchKit.launcher.addProgressListener((p) ->
		{
			EventQueue.invokeLater(() ->
			{
				progressBar.setVisible(true);

				if(p < 0 || p > 1)
				{
					progressBar.setIndeterminate(true);
				}

				else
				{
					progressBar.setIndeterminate(false);
					progressBar.setValue((int) (p * 100));
					
					if(play.isVisible())
					{
						play.setVisible(false);
					}
				}
			});
		});

		icon_syncing.setVisible(true);
		icon_synced.setVisible(false);

		J.a(() -> J.attempt(() ->
		{
			EventQueue.invokeLater(() ->
			{
				doit.setVisible(false);
			});
			
			LaunchKit.launcher.validate();

			EventQueue.invokeLater(() ->
			{
				icon_syncing.setVisible(false);
				icon_synced.setVisible(true);

				J.attempt(() ->
				{
					packname.setText(LaunchKit.launcher.getPack().getIdentity().getName());
					packv.setText(LaunchKit.launcher.getPack().getIdentity().getVersion());
					packname.setVisible(true);
					packv.setVisible(true);
				});
				
				login_pane.setVisible(!LaunchKit.launcher.isAuthenticated());
				doit.setVisible(!login_pane.isVisible());
				play.setVisible(!LaunchKit.launcher.isRunning());
				stopit.setVisible(LaunchKit.launcher.isRunning());
			});
		}));

		EventQueue.invokeLater(() ->
		{
			login_pane.setVisible(false);
		});

		J.a(() -> J.attempt(() ->
		{
			LaunchKit.launcher.authenticateWithToken();

			EventQueue.invokeLater(() ->
			{
				login_pane.setVisible(!LaunchKit.launcher.isAuthenticated());
				doit.setVisible(!login_pane.isVisible());
				play.setVisible(!LaunchKit.launcher.isRunning());
				stopit.setVisible(LaunchKit.launcher.isRunning());
				icon_connected.setVisible(LaunchKit.launcher.isAuthenticated());
				icon_disconnected.setVisible(!LaunchKit.launcher.isAuthenticated());
			});
		}));

		closeButton.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				System.exit(0);
			}
		});

		stopit.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				J.a(() -> J.attempt(() ->
				{
					LaunchKit.launcher.killGame();
					doit.setVisible(!login_pane.isVisible());
					play.setVisible(!LaunchKit.launcher.isRunning());
					stopit.setVisible(LaunchKit.launcher.isRunning());
					sound("stopit");
					Thread.sleep(700);
					EventQueue.invokeLater(() -> {
						play.setVisible(true);
					});
				}));
			}
		});

		play.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				J.a(() -> J.attempt(() ->
				{
					LaunchKit.launcher.launch();
					doit.setVisible(!login_pane.isVisible());
					play.setVisible(!LaunchKit.launcher.isRunning());
					stopit.setVisible(LaunchKit.launcher.isRunning());
				}));
			}
		});

		minimizeButton.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				setExtendedState(JFrame.ICONIFIED);
			}
		});

		login_ok.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{

				J.a(() -> J.attempt(() ->
				{
					EventQueue.invokeLater(() ->
					{
						login_pane.setVisible(false);
						icon_connected.setVisible(false);
						icon_disconnected.setVisible(false);
						progressBar.setVisible(true);
						progressBar.setIndeterminate(true);
						doit.setVisible(!login_pane.isVisible());
						play.setVisible(!LaunchKit.launcher.isRunning());
						stopit.setVisible(LaunchKit.launcher.isRunning());
					});

					J.attempt(() -> LaunchKit.launcher.authenticateWithCredentials(login_user.getText(), new String(login_pass.getPassword())));

					EventQueue.invokeLater(() ->
					{
						login_pane.setVisible(!LaunchKit.launcher.isAuthenticated());
						icon_connected.setVisible(LaunchKit.launcher.isAuthenticated());
						icon_disconnected.setVisible(!LaunchKit.launcher.isAuthenticated());
						progressBar.setVisible(false);
						progressBar.setIndeterminate(false);
						doit.setVisible(!login_pane.isVisible());
						play.setVisible(!LaunchKit.launcher.isRunning());
						stopit.setVisible(LaunchKit.launcher.isRunning());
					});
				}));

			}
		});

		login_cancel.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				EventQueue.invokeLater(() ->
				{
					login_pane.setVisible(false);
					icon_connected.setVisible(false);
					icon_disconnected.setVisible(true);
					doit.setVisible(!login_pane.isVisible());
					play.setVisible(!LaunchKit.launcher.isRunning());
					stopit.setVisible(LaunchKit.launcher.isRunning());
				});
			}
		});

		EventQueue.invokeLater(() ->
		{
			packname.setVisible(false);
			packv.setVisible(false);
		});
	}

	class MoveMouseListener implements MouseListener, MouseMotionListener
	{
		JComponent target;
		Point start_drag;
		Point start_loc;

		public MoveMouseListener(JComponent target)
		{
			this.target = target;
		}

		public JFrame getFrame(Container target)
		{
			if(target instanceof JFrame)
			{
				return (JFrame) target;
			}
			return getFrame(target.getParent());
		}

		Point getScreenLocation(MouseEvent e)
		{
			Point cursor = e.getPoint();
			Point target_location = this.target.getLocationOnScreen();
			return new Point((int) (target_location.getX() + cursor.getX()), (int) (target_location.getY() + cursor.getY()));
		}

		public void mouseClicked(MouseEvent e)
		{
		}

		public void mouseEntered(MouseEvent e)
		{
		}

		public void mouseExited(MouseEvent e)
		{
		}

		public void mousePressed(MouseEvent e)
		{
			this.start_drag = this.getScreenLocation(e);
			this.start_loc = this.getFrame(this.target).getLocation();
		}

		public void mouseReleased(MouseEvent e)
		{
		}

		public void mouseDragged(MouseEvent e)
		{
			Point current = this.getScreenLocation(e);
			Point offset = new Point((int) current.getX() - (int) start_drag.getX(), (int) current.getY() - (int) start_drag.getY());
			JFrame frame = this.getFrame(target);
			Point new_location = new Point((int) (this.start_loc.getX() + offset.getX()), (int) (this.start_loc.getY() + offset.getY()));
			frame.setLocation(new_location);
		}

		public void mouseMoved(MouseEvent e)
		{
		}
	}

	private void sound(String name)
	{
		J.a(() -> J.attempt(() ->
		{
			Clip sound = (Clip) AudioSystem.getLine(new Line.Info(Clip.class));
			sound.open(AudioSystem.getAudioInputStream(new BufferedInputStream(LKUI.class.getResourceAsStream("/org/cyberpwn/launchkit/gui/" + name + ".wav"))));
			sound.start();
		}));
	}
}
