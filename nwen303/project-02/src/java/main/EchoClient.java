/**
 * An echo client. The client enters data to the server, and the
 * server echoes the data back to the client.\
 *
 * This has been simplified, what the client sends is now hardcoded.
 *
 */

import java.net.*;
import java.io.*;

public class EchoClient
{

	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			System.err.println("Usage: java EchoClient <IP address> <Port number>");
			System.exit(0);
		}

		BufferedReader in = null;
		PrintWriter out = null;
		Socket sock = null;

		try {
			sock = new Socket(args[0], Integer.parseInt(args[1]));

			// set up the necessary communication channels
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new PrintWriter(sock.getOutputStream(),true);

			// send a sequence of messages and print the replies
			out.println("Knock, knock");
			System.out.println(in.readLine());
			out.println("Justin");
			System.out.println(in.readLine());
			out.println("Just in the neighborhood, thought I would drop by.");
			System.out.println(in.readLine());
			out.println("bye");
		}
		catch (IOException ioe) {
			System.err.println(ioe);
		}
		finally {
			if (in != null)
				in.close();
			if (out != null)
				out.close();
			if (sock != null)
				sock.close();
		}
	}
}
