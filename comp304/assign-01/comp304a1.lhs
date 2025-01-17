% COMP 304 Assignment 1
% David Barnett (300313764)

\section{1. Variations on a search}

\subsection{A) count}

Using pattern matching and grauds because it shows the different
cases the aligorithm will face

> count :: (Num a, Eq a) => a -> [a] -> Int 
> count _ [] = 0
> count n (x:xs)
>           | n == x = 1 + count n xs
>           | otherwise = count n xs

An alternative method would be to use if/else blocks
instead of mathcing on values.

An alternative method would be to use a left fold, which would
use more of the standard library, such as:

> countFold :: (Num a, Eq a) => a -> [a] -> a
> countFold n arr = foldl (\acc x -> if x == n then acc + 1 else acc) 0 arr

Another apporach would be to use a more tail recursive friendly version
such as:

> countTail :: (Num a, Eq a) => a -> [a] -> Int 
> countTail n arr = countTail' n arr 0
>           where countTail' _ [] t = t
>                 countTail' n (x:xs) t
>                       | x == n = countTail' n xs (t+1)
>                       | otherwise = countTail' n xs t

\subsection{B) allPos}

Using pattern matching and grauds because it shows the different
cases the aligorithm will face. A sub function
was used for the recursion to keep track of the index in
the array.

> allPos :: (Num a, Eq a) => a -> [a] -> [Int]
> allPos n arr = (allPosI n arr 1)
>
> -- n - is the number we are looking for
> -- arr - is the reminding array we are checking
> -- i - is the current index in the array
> -- returns an array of in where n was found in the array
> allPosI :: (Eq a) => a -> [a] -> Int -> [Int]
> allPosI _ [] _ = []
> allPosI n (x:xs) i
>               | n == x = i:(allPosI n xs (i+1))
>               | otherwise = allPosI n xs (i+1)


An alternative method would be to use if/else blocks
instead of mathcing on values or use a standard
list function or using forward propergating by passing the
array that is being built to each recursion call.

> allPosW :: (Num a, Eq a) => a -> [a] -> [Int]
> allPosW n arr = collect 1 arr
>       where collect _ [] = []
>             collect i (x:xs)
>                       | n == x = i:(collect (i+1) xs)
>                       | otherwise = (collect (i+1) xs)

\subsection{C) firstLastPos}

This method breaks down the problem into two sub-problems of
finding the 1st and last elemnts seperatly for ease of mind.

> -- n - is the number we are looking for
> -- arr - is the reminding array we are checking
> -- returns a tuple of the first and last indices where we saw n
> firstLastPos :: (Eq a) => a -> [a] -> (Int, Int)
> firstLastPos _ [] = (0,0)
> firstLastPos n arr = (firstPos n arr 1, lastPos n arr 1 0)
>
> -- n - is the number we are looking for
> -- arr - is the reminding array we are checking
> -- i - is the current index in the array
> -- returns the first index of the 1st instance of an element equalling n
> firstPos :: (Eq a) => a -> [a] -> Int -> Int
> firstPos _ [] _ = 0
> firstPos n (x:xs) i
>               | n == x = i
>               | otherwise = firstPos n xs (i+1)
>
> -- n - is the number we are looking for
> -- arr - is the reminding array we are checking
> -- i - is the current index in the array
> -- h - is the highest index we saw n being equal to
> -- returns the last index of the 1st instance of an element equalling n
> lastPos :: (Eq a) => a -> [a] -> Int -> Int -> Int
> lastPos _ [] _ h = h
> lastPos n (x:xs) i h
>               | n == x = lastPos n xs (i+1) i
>               | otherwise = lastPos n xs (i+1) h

An alternative method with be to join lastPos and firstPos together
but at the cost of doubling the number of cases to match against and greatly
increasing the complexity of the function or reduce the number of functions
by using where's but still iterating through the list twice at most.

> firstLastPosW :: (Eq a) => a -> [a] -> (Int, Int)
> firstLastPosW n arr = (firstPosW 1 arr, lastPosW 1 arr)
>       where firstPosW _ [] = 0
>             firstPosW i (x:xr)
>                   | x == n = i
>                   | otherwise = firstPosW (i+1) xr
>             lastPosW _ [] = 0
>             lastPosW i (x:xr)
>                   | x == n = max i (lastPosW (i+1) xr)
>                   | otherwise = lastPosW (i+1) xr

\section{2. Sorting}

\subsection{A) n^2 sorting aligorithm (Selection sort)}

this method finds the minimum value of the array and moves
it to the back of the sorted array. I chose selection sort
as I understood how it worked and how to pair haskell's recursion
to implement it.

> sort1 :: (Ord a) => [a] -> [a]
> sort1 [] = []
> sort1 arr = (minimum arr):sort1 (deleteFirst (minimum arr) arr)
>       where deleteFirst _ [] = []
>             deleteFirst n (x:xs)
>                 | x == n = xs
>                 | otherwise = x:deleteFirst n xs

Alternatives to implement selection sort would be to use a
recursive fold with the accumulator being a tuple of minimum and
the remaining list.

To add support for a first-order function to compare using a passed
in funciton the usage of minimum would need to be expanded into its own
function so it could use the aribitray comparator.
Such as:

> sort1ho :: (a -> a -> Ordering) -> [a] -> [a]
> sort1ho _ [] = []
> sort1ho cmp arr = (small cmp arr):sort1ho cmp (deleteFirst cmp (small cmp arr) arr)
>       where deleteFirst _ _ [] = []
>             deleteFirst c n (x:xs)
>                 | (c n x) == EQ = xs
>                 | otherwise = x:deleteFirst c n xs
>             small c (x:xs) = foldl(\min i -> if (c i min) == LT then i else min) x xs

\subsection{B) n log n sorting aligorithm (Quick sort)}

This method implements quick sort with the assumption of the
pivot point is the first element of the array. I chose this method
becaise it was the most simple version of quick sort I knew and recently
saw in a tutorial on Haskell.

> sort2 :: (Ord a) => [a] -> [a]
> sort2 [] = []
> sort2 (x:xr) = (sort2 less) ++ x:equal ++ (sort2 more)
>       where less  = filter(< x) xr
>             more  = filter(> x) xr
>             equal = filter(== x) xr
>

Using higher order functions to compare the parameters

> sort2ho :: (a -> a -> Ordering) -> [a] -> [a]
> sort2ho _ [] = []
> sort2ho cmp (x:xr) = (sort2ho cmp less) ++ x:equal ++ (sort2ho cmp more)
>       where less  = filter(\y -> y `cmp` x == LT) xr
>             more  = filter(\y -> y `cmp` x == GT) xr
>             equal = filter(\y -> y `cmp` x == EQ) xr

Merge sort

> sort2merge :: (Ord a) => [a] -> [a]
> sort2merge [] = []
> sort2merge [a] = [a]
> sort2merge [a, b]
>       | a < b = [a, b]
>       | otherwise = [b ,a] 
> sort2merge arr = merge (sort2merge left) (sort2merge right)
>   where n     = length arr 
>         left  = take (n `div` 2) arr
>         right = drop (n `div` 2) arr 
>         merge [] [] = [] 
>         merge [] y = y 
>         merge x [] = x 
>         merge (x:xs) (y:ys)
>            | x > y = y:(merge (x:xs) ys) 
>            | otherwise = x:(merge xs (y:ys))
>  

\section{3. Map}

> type MapElement k v = (k, v)
> type Map k v = [MapElement k v]


> emptyMap :: Map k v
> emptyMap = []


> hasKey :: (Eq k) => k -> Map k v -> Bool
> hasKey _ [] = False
> hasKey k ((x,  _) :xs)
>           | x == k = True
>           | otherwise = hasKey k xs

Set Value keeps that the map will only have  

> setVal :: (Eq k) => k -> v -> Map k v -> Map k v
> setVal k v [] = [(k, v)]
> setVal k v ((x, y):xs)
>             | k == x = (x, v) : xs
>             | otherwise = (x, y) : setVal k v xs


> getVal :: (Eq k) => k -> Map k v -> v
> getVal k ((x, y):xs)
>           | k == x = y
>           |  otherwise = getVal k xs


> delKey :: (Eq k) => k -> Map k v -> Map k v
> delKey _ [] = []
> delKey k ((x, y):xs)
>           | k == x = xs
>           | otherwise = (x, y):xs


\section{4. Building a Map}

This methods builds up the map and updates its entries
as it recuses over the array. 

> buildMap :: (Eq k, Num v) => [k] -> Map k v
> buildMap [] = emptyMap
> buildMap arr = build arr emptyMap
>           where build [] map = map
>                 build (x:xs) map 
>                   | hasKey x map = build xs (setVal x ((getVal x map)+1) map)
>                   | otherwise = build xs (setVal x 1 map)

Another method to achieve this would be to count up and remove all each element
as it comes across it.


\section{Examples and testing}

tests for sort1 and sort2 do not include
tests with empty arrays as it caused compile errors that I cannot
figure out.

> testCount1 = count 1 [1,2,1,2,1] == 3
> testCount2 = count 5 [1,2,1,2,1] == 0
> testCount3 = count 5 [] == 0
>
> testAllPos1 = allPos 1 [1,2,1,2,1] == [1,3,5]
> testAllPos2 = allPos 5 [1,2,1,2,1] == []
> testAllPos3 = allPos 5 [] == []
>
> testFirstLastPos1 = firstLastPos 1 [1,2,1,2,1] == (1,5)
> testFirstLastPos2 = firstLastPos 2 [1,2,1,2,1] == (2,4)
> testFirstLastPos3 = firstLastPos 5 [1,2,1,2,1] == (0,0)
> testFirstLastPos4 = firstLastPos 5 [] == (0,0)
>
>
> testSort11 = sort1 [1,2,1,2,1] == [1,1,1,2,2]
> testSort12 = sort1 [102,22,81,22,1] == [1, 22, 22, 81, 102]
>
> testSort21 = sort2 [1,2,1,2,1] == [1,1,1,2,2]
> testSort22 = sort2 [102,22,81,22,1] == [1, 22, 22, 81, 102]
>
> testBuildMap1 = buildMap [1,2,3,2,1] == [(1,2), (2, 2), (3,1)]

> main :: IO()
> main = do
>       -- Tests
>       print(testCount1)
>       print(testCount2)
>       print(testCount3)
>
>       print(testAllPos1)
>       print(testAllPos2)
>       print(testAllPos3)
>
>       print(testFirstLastPos1)
>       print(testFirstLastPos2)
>       print(testFirstLastPos3)
>       print(testFirstLastPos4)
>
>       print(testSort11)
>       print(testSort12)
>
>       print(testSort21)
>       print(testSort22)
>
>       print(testBuildMap1)
>
>       -- Count Examples
>       print("count 1 [1,2,1,2,1] == 3")
>       print(count 1 [1,2,1,2,1])
>
>       print("count 5 [1,2,1,2,1] == 0")
>       print(count 5 [1,2,1,2,1])
>
>       print("count 5 [] == 0")
>       print(count 5 [])
>
>       -- allPos Examples
>       print("allPos 1 [1,2,1,2,1] == [1,3,5]")
>       print(allPos 1 [1,2,1,2,1])
>
>       print("allPos 3 [1,2,1] == []")
>       print(allPos 3 [1,2,1])
>
>       -- firstLastPos Examples
>       print("firstLastPos 1 [1,2,1,2,1] == (1,5)")
>       print(firstLastPos 1 [1,2,1,2,1])
>
>       print("firstLastPos 2 [1,2,1,2,1] == (2,4)")
>       print(firstLastPos 2 [1,2,1,2,1])
>
>       print("firstLastPos 5 [1,2,1,2,1] == (0,0)")
>       print(firstLastPos 5 [1,2,1,2,1])
>
>       -- sorting
>       print("sort1 [1,2,1,2,1] == [1,1,1,2,2]")
>       print(sort1 [1,2,1,2,1])
>
>       print("sort1 [102,22,81,22,1] == [1,22,26,81,102]")
>       print(sort1 [102,22,81,26,1])
>
>       -- Inserstion sort with higher order functions (reversing the order)
>       print("sort1ho (\\l r -> compare r l) [1,2,1,2,1] == [2,2,1,1,1]")
>       print(sort1ho (\l r -> compare r l) [1,2,1,2,1])
>
>       print("sort1ho (\\l r -> compare r l)  [102,22,81,22,1] == [102,81,26,22,1]")
>       print(sort1ho (\l r -> compare r l) [102,22,81,26,1])
>
>       print("sort2 [1,2,1,2,1] == [1,1,1,2,2]")
>       print(sort2 [1,2,1,2,1])
>
>       print("sort2 [102,22,81,22,1] == [1,22,26,81,102]")
>       print(sort2 [102,22,81,26,1])
>
>       -- Quick sort with higer order functions (serversing the order)
>       print("sort2ho (\\l r -> compare r l) [1,2,1,2,1] == [2,2,1,1,1]")
>       print(sort2ho (\l r -> compare r l) [1,2,1,2,1])
>
>       print("sort2ho (\\l r -> compare r l)  [102,22,81,22,1] == [102,81,26,22,1]")
>       print(sort2ho (\l r -> compare r l) [102,22,81,26,1])
>
>       -- Map
>       print("Add Value")
>       print(setVal 10 "wow" emptyMap)
>       print("Delete Value")
>       print(delKey 10 (setVal 10 "wow" emptyMap))
>       print("Get Value")
>       print(getVal 10 (setVal 10 "wow" emptyMap))
>
>       -- Build Map
>       print("Build map")
>       print(buildMap [1, 2, 3, 2, 1])
