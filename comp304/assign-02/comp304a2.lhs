% David Barnett (300313264)
% COMP304 Assignment 2

\section{1. Binary Trees}

> import qualified Data.List as L
> data BinTree a = Empty | Node a (BinTree a) (BinTree a)
>              deriving (Show, Eq)

`Eq` for BinTree has only been used for the sanity of testing.

\subsection{a. hasbt}

> hasbt :: (Eq a) => a -> BinTree a -> Bool
> hasbt _ Empty = False
> hasbt a (Node x l r)
>       | a == x = True
>       | otherwise = hasbt a l || hasbt a r

recurises through the binary tree 

\subsection{b. equalbt}

> equalbt :: (Eq a) => BinTree a -> BinTree a -> Bool
> equalbt Empty Empty = True
> equalbt (Node _ _ _) Empty = False
> equalbt Empty (Node _ _ _) = False
> equalbt (Node lx ll lr) (Node rx rl rr)
>       | lx == rx = equalbt ll rl && equalbt lr rr
>       | otherwise = False

Could of just derived `Eq` and used `==` instead but decided that would defeat
the purpose.

\subsection{c. reflectbt}

> reflectbt :: BinTree a -> BinTree a
> reflectbt Empty = Empty
> reflectbt (Node x l r) = Node x (reflectbt r) (reflectbt l)

swaps the positions of sub-trees

\subsection{d. fringebt}

> fringebt :: BinTree a -> [a]
> fringebt Empty = []
> fringebt (Node a Empty Empty) = [a]
> fringebt (Node _ l r) = fringebt l ++ fringebt r

If the node has any children it is not on the fringe and composes
the result of the fringe of the left and right sub-trees.
Only if the node has no children it will return an array with the
node's value. This builds up a list of values of all leafs, in ascending
ordering assuming bt is a BST.

\subsection{e. fullbt}

> fullbt :: BinTree a -> Bool
> fullbt Empty = True
> fullbt (Node _ Empty (Node _ _ _)) = False
> fullbt (Node _ (Node _ _ _) Empty) = False
> fullbt (Node _ l r) = fullbt l && fullbt r

Checks if the tree is full by checking if every sub-tree
has either two children or none.

\section{2. Binary tree folds}

\subsection{a. btfold}

> btfold :: (a -> b -> b -> b) -> b -> BinTree a -> b
> btfold _ u Empty = u
> btfold f u (Node a l r) = f a (btfold f u l) (btfold f u r)

The `btfold` applies a function over the entire tree from leave up to root.
The higher order function has 3 parameters. The first is the value of the
node which the function is currently being applied on and the second parameter
is the result of the fold on the left sub-tree (or the unit value if it is empty) and the
third parameter is the result of the fold on the right sub-tree (or unit value if it is
empty). The higher order function returns the new value to be used in its parent's
fold or is the return if it is the root. The second parameter for `btfold` is the unit
value of the fold which is the value of the empty node. The third parameter is the `BinTree`
to apply the `btfold` over.

\subsection{b. Question 1 using btfold}

\subsubsection{hasbtf}

> hasbtf :: (Eq a) => a -> BinTree a -> Bool
> hasbtf x t = btfold (\u v w -> x == u || v || w) False t

Checks if the BinTree has the element `x` by bubbling up the True value
by OR'ing the result at each node.

\subsubsection{equalbtf}

This is not possible to implement using `btfold` as it requires traversing
both BinTrees in step lock. An alternative fold function to implement
this behaviour would be quite spesific with each and would almost be
just the same as using a higher order function to equate elements in
the two trees, such as:

> equalbtff :: (a -> a -> Bool) -> BinTree a -> BinTree a -> Bool
> equalbtff _ Empty Empty = True
> equalbtff _ (Node _ _ _) Empty = False
> equalbtff _ Empty (Node _ _ _) = False
> equalbtff f (Node lx ll lr) (Node rx rl rr)
>       | f lx rx = equalbtff f ll rl && equalbtff f lr rr
>       | otherwise = False

\subsubsection{reflectbtf}

> reflectbtf :: BinTree a -> BinTree a
> reflectbtf t = btfold (\u v w -> Node u w v) Empty t

`reflectbtf` just swaps the sub-trees around as it makes its way up the fold.

\subsubsection{fringebtf}

> fringebtf :: BinTree a -> [a]
> fringebtf t = btfold (\u v w -> if length v == 0 && length w == 0 then [u] else v ++ w) [] t

Would of thought to use `v == [] && w == []` instead but this introdued
the need for the `Eq` type class so chose to use lenght of the arrays to
keep the same type contraints as the non-fold version of the function.

\subsubsection{fullbtf}

Using the ordering data type because it has 3 enumations.
With the 3 enumations we can check for difference between
the unit/ empty sub-trees, a full subtree and a not full subtree
which bubbles all the way up the fold.

> fullbtf :: BinTree a -> Bool
> fullbtf Empty = True
> fullbtf t = (btfold ffold LT t) == EQ
>           where
>               ffold _ LT LT = EQ
>               ffold _ EQ EQ = EQ
>               ffold _ _ _ = GT


\section{3. Binary Search Trees}

\subsection{empty}

> empty :: (Ord a, Eq a) => BinTree a
> empty = Empty

\subsection{insert}

> insert :: (Ord a, Eq a) => a -> BinTree a -> BinTree a
> insert x Empty = (Node x Empty Empty)
> insert x (Node a l r)
>   | x > a     = Node a l (insert x r)
>   | x == a    = Node a l r
>   | otherwise = Node a (insert x l) r

Inserts the new value as a leaf positioned so it maintains the
ordering of the tree.

\subsection{has}

Using a binary search to find the element

> has :: (Ord a, Eq a) => a -> BinTree a -> Bool
> has _ Empty = False
> has x (Node a l r)
>   | x > a     = has x r
>   | x == a    = True
>   | otherwise = has x l

\subsection{delete}

> delete :: (Ord a, Eq a) => a -> BinTree a -> BinTree a
> delete _ Empty = Empty
> delete x (Node a Empty Empty)
>       | x == a = Empty
>       | otherwise = Node a Empty Empty
> delete x (Node a l Empty)
>       | x == a = l
>       | x < a = Node a (delete x l) Empty
>       | otherwise = Node a l Empty
> delete x (Node a Empty r)
>       | x == a = r
>       | x > a = Node a Empty (delete x r)
>       | otherwise = Node a Empty r
> delete x (Node a l r)
>       | x == a = Node (smallest r) l (delete (smallest r) r)
>       | x > a = Node a (delete x l) r
>       | x < a = Node a l (delete x r)
>       where
>           smallest (Node a Empty _) = a
>           smallest (Node a l _) = smallest l

Covering all three cases of no children, one child and two children
in separate patterns. This version of delete favours the
right hand side to find a suitable node to be promoted when removing with
two children.

\subsection{flatten}

> flatten :: BinTree a -> [a]
> flatten Empty = []
> flatten (Node a l r) = (flatten l) ++ [a] ++ (flatten r)

`flatten` collects all the values of the `BinTree` by inserting the
current node's value in-between the left and right sub-tree. This also
maintains ascending ordering of the array at the end.

\subsection{equals}

Uses the implementation detail of flatten that it is in
descending order so even if the BST's are inserted in different
orders the flatten arrays will be the same so we can equate on them
for simplicity.

> equals :: (Eq a) => BinTree a -> BinTree a -> Bool
> equals a b = (flatten a) == (flatten b)

\section{Graph Algorithms}

> type Graph a = [(a, Int, a)]

\subsubsection{reachable}

> reachable :: (Eq a) =>  a -> a -> Graph a -> Bool
> reachable _ _ [] = False
> reachable x y g  = not ((paths x y g []) == [])

Paths find all possible paths from A to Z.
It achieves this by passing the visited set, which is also the path taken to get
to the current vertex, to each subsequence recursion and stepping forward with
where the path is from.

This uses `Data.List` for `concatMap`

> paths :: (Eq a) => a -> a -> Graph a -> Graph a -> [Graph a] 
> paths a b g p
>   | a == b = [reverse p]
>   | otherwise = L.concatMap (\e@(x, z, y) -> if x == a then (paths y b g (e:p)) else (paths x b g (e:p))) (unvisited (neighbours a g) p)
>   where unvisited  n v = filter (\a -> not (elem a v)) n
>         neighbours b g = filter (\(x, _, y) -> x == b || y == b) g

To implement the undirected graph neighbours checks if either side of the edge containts the
current vertex and then in the `concatMap` we have to check if we need to move forward using the
first or second vertex.

Another way of implementing this would be to make a function that will turn an directed graph
and add new edges to make it into a undirected graph.

\subsubsection{minCostPath}

> minCostPath :: (Eq a) => a -> a -> Graph a -> Graph a
> minCostPath _ _ [] = []
> minCostPath x y g =  minn (paths x y g [])
>   where cost p = foldl (\a (_, c, _) -> a + c) 0 p
>         minn [] = []
>         minn g  = L.minimumBy (\a b -> compare (cost a) (cost b)) g

This uses `Data.List` for `minimumBy`

With lazy evaluation the computation of the `reachable` function is cheaper
than computing the `minCostPath` function. This is cause by lazy evaluation
only computing what it needs and for the `reachable` function to determine
a result it only has to find a single path from `paths` where `minCostPath`
must evaluate all paths as they are needed to compute which one is the shortest
path.

\subsubsection{cliques}

> cliques :: (Eq a) => Graph a -> [Graph a]
> cliques [] = []
> cliques g = L.groupBy (\ (y,_,a) (b,_,x) -> reachable a b g) g

Groups components of the graph if they are both reachable from A -> B and  B -> A
so that any element of the sub-graph is reachable to and from any other vertices
in the component.

\section{Examples}

\subsection{Tests}

> testhasbtEmpty  = hasbt 1 empty == False
> testhasbtHasTop = hasbt 1 (insert 1 empty) == True
> testhasbtHas2nd = hasbt 1 (insert 1 (insert 2 empty)) == True

> testequalbtEmpty   = equalbt (Empty :: BinTree Int) (Empty :: BinTree Int) == True
> testequalbtFilled1 = equalbt empty (insert 1 empty) == False
> testequalbtFilled2 = equalbt (insert 1 empty) empty == False

> testreflectbtEmpty = equals (reflectbt (Empty :: BinTree Int)) (Empty :: BinTree Int)
> testreflectbt1 = equals (reflectbt (insert 2 (insert 1 empty))) (Node 1 (Node 2 Empty Empty) Empty)

> testfringebtEmpty = fringebt (Empty :: BinTree Int) == ([] :: [Int])
> testfringebtFilled = fringebt (insert 2 (insert 1 empty)) == [2]

> testfullbtEmpty = fullbt (Empty :: BinTree Int) == True
> testfullbtUnBal = fullbt (insert 1 (insert 2 empty)) == False
> testfullbtBal = fullbt (insert 3 (insert 1 (insert 2 empty))) == True

> testhasbtfEmpty  = hasbtf 1 empty == False
> testhasbtfHasTop = hasbtf 1 (insert 1 empty) == True
> testhasbtfHas2nd = hasbtf 1 (insert 1 (insert 2 empty)) == True

> testreflectbtfEmpty = equals (reflectbtf (Empty :: BinTree Int)) (Empty :: BinTree Int)
> testreflectbtf1 = equals (reflectbtf (insert 2 (insert 1 empty))) (Node 1 (Node 2 Empty Empty) Empty)

> testfringebtfEmpty = fringebtf (Empty :: BinTree Int) == ([] :: [Int])
> testfringebtfFilled = fringebtf (insert 2 (insert 1 empty)) == [2]

> testfullbtfEmpty = fullbtf (Empty :: BinTree Int) == True
> testfullbtfUnBal = fullbtf (insert 1 (insert 2 empty)) == False
> testfullbtfBal = fullbtf (insert 3 (insert 1 (insert 2 empty))) == True

> testEmpty = empty == (Empty :: BinTree Int)

> testInsert1 = insert 1 empty == (Node 1 Empty Empty)
> testInsert2 = insert 2 (insert 1 empty) == Node 1 Empty (Node 2 Empty Empty)

> testHasEmpty = has 1 empty == False
> testHas1     = has 1 (insert 1 empty) == True
> testHas2     = has 2 (insert 1 empty) == False

> testDeleteEmpty = delete 1 empty == (Empty :: BinTree Int)
> testDelete1   = delete 1 (insert 1 empty) == (Empty :: BinTree Int)
> testDelete2   = delete 2 (insert 1 empty) == Node 1 Empty Empty
> testDelete3   = delete 2 (insert 2 (insert 1 empty)) == Node 1 Empty Empty
> testDelete4   = delete 2 (insert 3 (insert 2 (insert 1 empty))) == Node 1 Empty (Node 3 Empty Empty)
> testDelete5   = delete 3 (insert 2 (insert 3 (insert 1 empty))) == Node 1 Empty (Node 2 Empty Empty)

> testreachableEmpty = reachable 'A' 'B' [] == False
> testreachableValid1 = reachable 'A' 'B' [('A', 1, 'B')] == True
> testreachableValid2 = reachable 'A' 'B' [('A', 1, 'C'), ('A', 1, 'T'), ('C',1,'B')] == True
> testreachableValid3 = reachable 'A' 'B' [('A', 1, 'C'), ('B', 1, 'C')] == True
> testreachableInValid1 = reachable 'A' 'B' [('A', 1, 'C')] == False

> testminCostPathEmpty = minCostPath 'A' 'B' [] == []
> testminCostPathValid1 = minCostPath 'A' 'B' [('A', 1, 'B')] == [('A', 1, 'B')]
> testminCostPathValid2 = minCostPath 'A' 'B' [('A', 1, 'C'), ('A', 20, 'B'), ('C',1,'B')] == [('A', 1, 'C'), ('C', 1, 'B')]
> testminCostPathInValid1 = minCostPath 'A' 'B' [('A', 1, 'C')] == []

> testCliquesEmpty    = cliques ([] :: Graph Char) == [] 
> testCliquesValid1   = cliques [('A', 1, 'B'), ('B', 1, 'A')] == [[('A', 1, 'B'), ('B', 1, 'A')]]
> testCliquesValid2   = cliques [('A', 1, 'B'), ('B', 1, 'A'), ('C',1,'Z')] == [[('A', 1, 'B'), ('B', 1, 'A')], [('C',1,'Z')]]

> main = do
>  putStrLn("hasbt Test")
>  print(testhasbtEmpty)
>  print(testhasbtHasTop)
>  print(testhasbtHas2nd)
>
>  putStrLn("\nequalbt Test")
>  print(testequalbtEmpty)
>  print(testequalbtFilled1)
>  print(testequalbtFilled2)
>
>  putStrLn("\nreflectbt Test")
>  print(testreflectbtEmpty)
>  print(testreflectbt1)
>
>  putStrLn("\nfringebt Test")
>  print(testfringebtEmpty)
>  print(testfringebtFilled)
>
>  putStrLn("\nfullbt Test")
>  print(testfullbtEmpty)
>  print(testfullbtUnBal)
>  print(testfullbtBal)
>
>  putStrLn("\nhasbtf Test")
>  print(testhasbtfEmpty)
>  print(testhasbtfHasTop)
>  print(testhasbtfHas2nd)
>
>  putStrLn("\nreflectbtf Test")
>  print(testreflectbtfEmpty)
>  print(testreflectbtf1)
>
>  putStrLn("\nfringebtf Test")
>  print(testfringebtfEmpty)
>  print(testfringebtfFilled)
>
>  putStrLn("\nfullbtf Test")
>  print(testfullbtfEmpty)
>  print(testfullbtfUnBal)
>  print(testfullbtfBal)
>
>  putStrLn("\nempty Test")
>  print(testEmpty)
>
>  putStrLn("\ninsert Test")
>  print(testInsert1)
>  print(testInsert2)
>
>  putStrLn("\nHas Test")
>  print(testHasEmpty)
>  print(testHas1)
>  print(testHas2)
>
>  putStrLn("\nDelete Test")
>  print(testDeleteEmpty)
>  print(testDelete1)
>  print(testDelete2)
>  print(testDelete3)
>  print(testDelete4)
>  print(testDelete5)
>
>  putStrLn("\nreachable Test")
>  print(testreachableEmpty)
>  print(testreachableValid1)
>  print(testreachableValid2)
>  print(testreachableValid3)
>  print(testreachableInValid1)
>
>  putStrLn("\nminCostPath Test")
>  print(testminCostPathEmpty)
>  print(testminCostPathValid1)
>  print(testminCostPathValid2)
>  print(testminCostPathInValid1)
>
>  putStrLn("\nminCostPath Test")
>  print(testminCostPathEmpty)
>  print(testminCostPathValid1)
>  print(testminCostPathValid2)
>  print(testminCostPathInValid1)
>
>  putStrLn("\ncliques Test")
>  print(testCliquesEmpty)
>  print(testCliquesValid1)
>  print(testCliquesValid2)
