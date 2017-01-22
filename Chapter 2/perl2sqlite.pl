#!/usr/bin/perl

## If you're on a Mac or a Linux machine, you almost surely have Perl installed; but
## you'd require some Perl libraries to run. Here's how you'd install them. 
## From the command-line:
## 
##  sudo cpan
##  cpan[1]>install DBI
##  cpan[1]>install DBD::SQLite
##  ...
##  cpan[1]>quit

## Usage: perl perl2sqlite.pl

### Standard app-2-RDBMS software architecture:
##
##              software library: the 'middleware'
##                          /
##                   +-------------+
##    app<---------->| DB 'driver' |<------------>RDBMS
##                   +-------------+
##

use DBI;      ## Perl's standard interface for DB connections
use strict;
use warnings;

# URI for connection.
my $uri = "DBI:SQLite:dbname=recClub.db";

## Connect to DB
my $dbh = DBI->connect($uri) or die $DBI::errstr;
print "\nrcClub.db opened...\n\n";

## Create an executable SQL statement.
my $sql = 'SELECT * FROM activities';
my $stmt = $dbh->prepare($sql);

## Execute the statement, capturing the status value returned.
my $result = $stmt->execute() or die $DBI::errstr; ## problem executing the query
print $DBI::errstr if $result < 0;                 ## check for non-fatal error

## Iterate over the rows of the activities table.
while(my @row = $stmt->fetchrow_array()) {
    printf("%.2d %s\n", $row[0], $row[1]);  ## $row[0] is 1st col (act_id), $row[1] is 2nd col (name)
}

$dbh->disconnect(); ## clean up
