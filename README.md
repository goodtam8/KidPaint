# KidPaint_Network

KidPaint is a painting application designed for children, allowing them to draw and paint using pen or bucket tools on a digital sketchpad.

## Project Deadlines:

- Initial Setup: November 4
- 50% Completion: November 9
- Basic Features Completion: November 15
- **Final Deadline: November 26**

## Features: Basic Client-Server Model

1. A server operates within the same subnet, without a graphical user interface.
2. The KidPaint application functions as a client-side program.
3. Upon launching KidPaint, users input their username via a graphical interface. The client broadcasts a network request using UDP.
4. The server replies with its IP address and port number via a UDP packet.
5. The client establishes a TCP connection with the server upon receiving its reply, downloading sketch data which is displayed on the client's sketchpad.
6. Users do not need to manually input network settings such as server IP or port number.
7. The client sends TCP packets with updates to the server when drawings are made on the sketchpad.
8. The client receives TCP updates from the server reflecting sketches made by other users, applying these updates to its sketchpad.
9. When a user types a message and presses ENTER, the client sends a TCP packet to the server.
10. The client receives TCP messages from the server if other users send messages, displaying them in the chat area with the sender's name.
11. A button should be added for saving sketches locally.
12. A button should be added for loading sketches from a local file, sending the data to the server, and updating all connected clients' sketchpads.
13. All users draw on the same sketch through this approach.

## Overview

1. Types of Messages:

- 0 - Chat message
- 1 - Drawing message
- 2 - Bucket data
- 3 - Sketch data
- 4 - Clear Data

## Protocols

2. Forward Drawing Message Protocol

- Send message length
- Send message type
- Send message content (loop)

3. Forward Text Message Protocol

## Step-by-Step Process

1. (Client) User inputs name and broadcasts it via UDP.
2. (Server) Server responds with IP address.
3. (Client) Client receives IP address and requests TCP connection.
4. (Server) Server establishes TCP connection, enables multithreading, and sends available studios.
5. (Client) User selects a studio.
6. (Server) Server creates a studio if not listed, adds clientSocket, and sends existing sketchData.
7. (Client) User interacts by drawing, sending messages, loading, or saving.
8. (Server) Server processes inputs and broadcasts to clients in the same studio.

## Additional Features:

1. Clear all paintings.
2. Right-click changes color.
3. Users can select pen size and color, coloring multiple circles simultaneously if pen size is set to 3.
4. Stores images as actual pictures, not just coordinates.

## TODO:

- [ ] Verify clear drawing functionality.
- [ ] Fix visibility of clear button (visible only when window is maximized).
- [ ] Check new studio creation functionality.

## TODO 2:

- [ ] Add new features.

## Less Important:

- [ ] Optimize methods for efficiency (reduce arrays, increase speed).
- [ ] Make methods reusable.
- [ ] Improve UI of Studio Choosing Panel.
- [ ] Display studio name prominently.

## Issues:

1. [x] Load: Saves only in 40x30, not 50x50.
2. [x] Bucket coloring exceeds borders.
3. [x] Send sketch data beyond limits.
4. [x] Disconnect issues.
5. Handle drawing that existed before new connections.

## DONE:

- [x] Set up initial connection draft.
- [x] Prompt for username.
- [x] Integrate username into messaging system.
- [x] Assign username correctly.
- [x] Handle bucket data.
- [x] Enable data saving.
- [x] Save only the image part.
- [x] Load and send all pixels and colors.
- [x] Load data sent to all users.
- [x] Create username prompt frame.
- [x] Send bucket data.
- [x] Retrieve original drawing, maintain data array, and send to server.

## Do Not

1. Avoid implementing undo functionality (overriding, etc.).

# Draft Notes:

## Implemented Features:

1. Upon receiving a server reply, the client establishes a TCP connection, downloading and rendering sketch data on its sketchpad.

- Server Side:

Added a List<Integer> sketchData to store sketch information.

In forwardDrawingMessage, code was added to store color, x, and y values in sketchData.

Added sendSketchData method to transmit sketch data upon client connection, executed in the client handling thread.

11. Messages display immediately in chat with sender’s name.

- Client Side:

Store username and append when sending text messages.

Send and receive bucket data, forwarding it to and from the server.

Client Side:

- Identify painted pixels.
- Loop through list to send value sizes.
- Send color and type.

Server:

- Receive type.
- Receive values (loop).
- Receive color.

- Loop through sockets.

## Tools:

- Graphics2D
