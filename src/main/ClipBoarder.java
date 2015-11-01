package main;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Created by Anna on 31/10/2015.
 */
public class ClipBoarder implements Runnable
{
	@Override
	public void run()
	{
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();

		while (ClipBoarder.running)
		{
			String result = null;
			try
			{
				result = (String) clipboard.getData(DataFlavor.stringFlavor);
			}
			catch (UnsupportedFlavorException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

			if (result.equals("quit"))
			{
				ClipBoarder.running = false;
				PoEItemAnalyzer.running = false;
			}
			else if (!result.equals(ClipBoarder.lastCB))
			{
				ClipBoarder.lastCB = result;
				if (ClipBoarder.lastCB.contains(key1) &&
//					ClipBoarder.lastCB.contains(key2) &&
					ClipBoarder.lastCB.contains(key3))
				{
//							System.out.println("String from Clipboard: " + result);
					PoEItemAnalyzer.queue.offer(result);
				}
			}

			try
			{
				Thread.sleep(50);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}


	public static boolean running = true;
	public static String  lastCB  = "";
	public static String  key1    = "Rarity:";
	public static String  key2    = "Requirements:";
	public static String  key3    = "Item Level:";
}
