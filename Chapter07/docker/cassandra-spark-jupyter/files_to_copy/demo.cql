CREATE KEYSPACE demo WITH replication = {'class': 'NetworkTopologyStrategy', 'dc1': 1}  AND durable_writes = true;
use demo;
CREATE TABLE IF NOT EXISTS offers (
  offerid text,
  itemid text,
  PRIMARY KEY (offerid, itemid)
);
INSERT INTO offers(offerid, itemid) VALUES ('ofr1', 'item1');
INSERT INTO offers(offerid, itemid) VALUES ('ofr1', 'item2');
INSERT INTO offers(offerid, itemid) VALUES ('ofr1', 'item3');
INSERT INTO offers(offerid, itemid) VALUES ('ofr1', 'item4');
INSERT INTO offers(offerid, itemid) VALUES ('ofr1', 'item5');
INSERT INTO offers(offerid, itemid) VALUES ('ofr2', 'item6');
INSERT INTO offers(offerid, itemid) VALUES ('ofr2', 'item7');
INSERT INTO offers(offerid, itemid) VALUES ('ofr3', 'item8');
INSERT INTO offers(offerid, itemid) VALUES ('ofr4', 'item9');
CREATE TABLE IF NOT EXISTS orders (
  userid text,
  itemid text,
  PRIMARY KEY (userid, itemid)
);
INSERT INTO orders(userid, itemid) VALUES ('usr1', 'item2');
INSERT INTO orders(userid, itemid) VALUES ('usr1', 'item3');
INSERT INTO orders(userid, itemid) VALUES ('usr1', 'item7');
INSERT INTO orders(userid, itemid) VALUES ('usr1', 'item8');
INSERT INTO orders(userid, itemid) VALUES ('usr2', 'item6');
INSERT INTO orders(userid, itemid) VALUES ('usr2', 'item9');
INSERT INTO orders(userid, itemid) VALUES ('usr3', 'item1');
INSERT INTO orders(userid, itemid) VALUES ('usr3', 'item6');
INSERT INTO orders(userid, itemid) VALUES ('usr3', 'item7');
INSERT INTO orders(userid, itemid) VALUES ('usr3', 'item8');
INSERT INTO orders(userid, itemid) VALUES ('usr3', 'item9');
