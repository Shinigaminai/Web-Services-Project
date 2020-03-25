INSERT INTO Users(userID, name) VALUES (1, 'Jeff');
INSERT INTO Users(userID, name) VALUES (2, 'BeschderMann');
INSERT INTO Users(userID, name) VALUES (3, 'GeilerTyp');

INSERT INTO PokeTeam(pokeTeamID, userID) VALUES (1, 2);
INSERT INTO PokeTeam(pokeTeamID, userID) VALUES (2, 3);
INSERT INTO PokeTeam(pokeTeamID, userID) VALUES (3, 1);

INSERT INTO Pokemon(pokemonID, internID, pokeTeamID) VALUES (1, 5865, 2);
INSERT INTO Pokemon(pokemonID, internID, pokeTeamID) VALUES (2, 51, 1);