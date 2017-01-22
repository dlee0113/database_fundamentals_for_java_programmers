/* depth-first search */

WITH RECURSIVE
  under_carol(name, level) AS (
    VALUES('Carol', 0)
    UNION ALL
    SELECT orgchart.name, under_carol.level+1
      FROM orgchart JOIN under_carol ON orgchart.reportsTo=under_carol.name
     ORDER BY 2 DESC                                                  
  )
SELECT SUBSTR('..........', 1, level * 3) || name FROM under_carol;   /* || is string concatenation */
