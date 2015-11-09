# grouping
Java implementation of single-linkage clustering. This code follows [the Wikipedia article on the subject](https://en.wikipedia.org/wiki/Single-linkage_clustering), implementing the naive, cubic algorithm to do the clustering. I didn't get around to doing the optimized, quadratic one yet.

The main class is [net.drmirror.GroupingMatrix](src/net/drmirror/GroupingMatrix.java). For a demo, see [net.drmirror.test.MatrixDemo](src/net/drmirror/test/MatrixDemo.java) and [net.drmirror.test.NumberMatrix](src/net/drmirror/test/NumberMatrix.java). 

# MatrixDemo Example

The [MatrixDemo](src/net/drmirror/MatrixDemo) uses a set of eight people from Duckburg and groups
them by similarity. 

First Name | Last Name | E-Mail           | Phone | Country
-----------|-----------|------------------|-------|--------
Donald     | Duck      | donald@duck.com  | 1234  | US
Daisy      | Duck      | daisy@duck.com   | 1234  | US
Scrooge    | Duck      | scrooge@duck.com | 7890  | US
Mickey     | Mouse     | mickey@mouse.com | 5678  | US
Minnie     | Mouse     | minnie@mouse.com | 6789  | US
Huey     | Duck       | kids@duck.com  | 1234  | US
Louie     | Duck      | kids@duck.com  | 1234  | US
Dewey     | Duck      | kids@duck.com  | 1234  | US

You can see that Donald and Daisy have different e-mail addresses but the same phone number. Scrooge has a different e-mail address *and* a different phone number and is thus "further away". Mickey and Minnie have different e-mail addresses and different phone numbers, but they do share the same last name. Finally, Huey, Louie and Dewey have the same e-mail address and the same phone number, which also happens to be the same as Donald's and Daisy's. (Any similarities with actual people from Duckburg are purely coincidental.)

As a distance function, we use the number of equal attributes between two records, normalized by the total number of attributes.  Thus, two records where all attributes are equal have a distance of 0, while two records where all attributes are different have a distance of 1.  If only one attribute is different, that's a distance of 0.2.

```java
	public double distance (TestItem a, TestItem b) {
		int matchCount = 0;
		if (a.firstName.equals(b.firstName)) matchCount++;
		if (a.lastName.equals(b.lastName)) matchCount++;
		if (a.email.equals(b.email)) matchCount++;
		if (a.phone.equals(b.phone)) matchCount++;
		if (a.country.equals(b.country)) matchCount++;
		return 1.0 - (double)matchCount / 5.0;
	}
```

Turning this into a GroupingMatrix yields the following structure:

```
               Donald      Daisy    Scrooge     Mickey     Minnie       Huey      Louie
Daisy           0.400
Scrooge         0.600      0.600
Mickey          0.800      0.800      0.800
Minnie          0.800      0.800      0.800      0.600
Huey            0.400      0.400      0.600      0.800      0.800
Louie           0.400      0.400      0.600      0.800      0.800      0.200
Dewey           0.400      0.400      0.600      0.800      0.800      0.200      0.200
```

We can see that Huey, Louie and Dewey form the tightest cluster with a distance of only 0.2 among each other (only the first name is different).  Donald and Daisy are close, but not *that* close (0.4).  Mickey and Minnie, at 0.6, are even further apart, and so on.

Calling the method ```mergeOnce(threshold)``` finds the closest pair in the set whose distance is below the threshold and merges them into one cluster, updating the distances to all other elements of the set to be the minimum of the distances of the individual elements.  For our people from Duckburg, calling ```mergeOnce()``` repeatedly with a threshold of 1.0 (no limits on merging) results in the following sequence of matrixes.

```
               Donald      Daisy    Scrooge     Mickey     Minnie      Dewey
Daisy           0.400
Scrooge         0.600      0.600
Mickey          0.800      0.800      0.800
Minnie          0.800      0.800      0.800      0.600
Dewey           0.400      0.400      0.600      0.800      0.800
H/L             0.400      0.400      0.600      0.800      0.800      0.200
```

Huey and Louie have been turned into a group, Dewey still sits at a distance of 0.2 from them.

```
               Donald      Daisy    Scrooge     Mickey     Minnie
Daisy           0.400
Scrooge         0.600      0.600
Mickey          0.800      0.800      0.800
Minnie          0.800      0.800      0.800      0.600
D/H/L           0.400      0.400      0.600      0.800      0.800
```

Now we have a group of Dewey, Huey and Louie (D/H/L), everybody else is still separate.  The following iterations first group Donald and Daisy, then put them into a group with Huey, Louie and Duey.

```
              Scrooge     Mickey     Minnie      D/H/L
Mickey          0.800
Minnie          0.800      0.600
D/H/L           0.600      0.800      0.800
D/D             0.600      0.800      0.800      0.400

              Scrooge     Mickey     Minnie
Mickey          0.800
Minnie          0.800      0.600
D/H/L/D/D       0.600      0.800      0.800
```

Next, Mickey and Minnie are grouped, while Scrooge remains separate.  He joins the Ducks in the next step, before finally, all inhabitants of Duckburg are clustered into one and the same group.

```
              Scrooge  D/H/L/D/D
D/H/L/D/D       0.600
M/M             0.800      0.800

                  M/M
S/D/H/L/D/D     0.800

M/M/S/D/H/L/D/D   ./.
```
