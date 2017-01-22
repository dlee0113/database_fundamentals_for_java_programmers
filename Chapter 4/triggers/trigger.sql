CREATE TABLE costchanges (cc_id INTEGER PRIMARY KEY AUTOINCREMENT, 
                          oldcost DECIMAL NOT NULL,      
                          newcost  DECIMAL NOT NULL,      
                          whenT DATE NOT NULL);          

CREATE TRIGGER activities_trigger UPDATE OF cost ON activities
  BEGIN
    INSERT INTO costchanges (oldcost, newcost, whenT) VALUES (old.cost, new.cost, CURRENT_TIMESTAMP);
  END;
