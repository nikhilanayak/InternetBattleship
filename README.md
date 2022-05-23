# Internet Battleship


[![forthebadge](https://forthebadge.com/images/badges/made-with-crayons.svg)](https://forthebadge.com)
[![forthebadge](https://forthebadge.com/images/badges/mom-made-pizza-rolls.svg)](https://forthebadge.com)
[![forthebadge](https://forthebadge.com/images/badges/powered-by-water.svg)](https://forthebadge.com)

## What Is Internet Battleship
Internet battleship is an implementation of the popular board game battleship, written in Java using Java Swing for rendering and Java sockets for networking.

## Installation
1. Clone This Repository or use the zip file provided to you.
```
git clone https://github.com/nikhilanayak/InternetBattleship.git
```

2. Run server. In your IDE(Preferrably IntelliJ or Eclipse), open the folder as a project and run `Server.java`
3. Run 2 instances of client. If you are running both instances of client locally, just run client twice. Otherwise, get another player to download the source code and run the client. Only one player needs to run the server.


## Usage
1. Connecting to the server

    When you run both instances of client, they will both prompt to enter an IP address, then a port. The IP address is the IP address of the machine that is running `Server.java` The port the server runs on is 1337.

2. Readying Up

    Once both players have connected to the server, they will see the main GUI. On the left, there is a button prompting the players to ready up. On the right, there should be 2 grids. The grid on the left is where your battleships go, and the grid on the right is where you put your guesses. Both players can now ready up.

3. Placing Ships

    Once both players are ready, they will be prompted to place their ships. Each player has 4 ships, of length 2, 3, 4, and 5. To place a ship, press a starting cell on the left grid. Then, press another cell on the left grid. If the cells form a vertical/horizontal line and are a valid length, that ship will be placed.

4. Playing The Game

    Once both players have placed all their ships, the game will start. Once it is your turn, you should get a prompt from the game that it's your turn. To play your turn, press a cell on the right grid. If the cell is red, that means you hit a ship. If the cell is blue, that means you missed. Make sure not to press the same cell twice, or your turn will be wasted. If your opponent hits a part of your ship, that part will become red on the left grid.

5. End Condition
    
    The game ends when one of the players has sunk all parts of all 4 ships. Once this happens, both players will get a win/lose message, and the game will end. The server will terminate the program, the sockets will disconnect, and the clients will close their GUIs.
