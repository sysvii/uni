Compiler and interpreter for simple straight line programs.

\section{Data types}

A straight line program is just a list of assignment statements, where an
expression can contain variables, integer constants and arithmetic operators.

> import qualified Data.List as L
> data Prog = Prog [Proc] Body
>             deriving (Show)
> data Body = Body Decl [Stmt]
>             deriving (Show)

While is added as an expression and a list of statements to always have the ability to
evaluate some value to check if the while should continue and the list of statements to
be able to wrap the necessary jump statements around the internal statements.

The If statement is similar to the While statement expect in how it uses an
expression to find out which branch it should be using and the list of statements
allow them to be wrapped with the necessary jump statements to give the correct behaviour.

> data Stmt = Asgn Var Exp
>      	    | While Exp [Stmt]
>      	    | If Exp [Stmt] [Stmt]
>      	    | Call ProcName [Exp]
>             deriving (Show)

> data Exp = Const Val
>          | ConstB Bool
>          | Var Char
>      	   | Bin Op Exp Exp
>      	   | ConNot Exp
>      	   | Con CondOp Exp Exp
>            deriving (Show)

> data Op = Plus | Minus | Times | Div
>           deriving (Show, Eq)

> type ProcName = [Char]
> data Proc = Proc ProcName Decl Body
>       deriving (Show)

The declaration section contains a pair of variable names and types
to be used throughout the program.

> type Decl = [(Var, Type)]

The `Type` ADT is used to represent the two different type
of variables that exist.

> data Type = TBool | TInt
>             deriving (Show, Eq)

> data CondOp = And | Or | Not
>               deriving (Show, Eq)

The store is a list of variable names and their values.  For now, a variable
now is just a single character and a value is an integer.

> type Store = [(Var, Val)]
> type StoreStack = [Store]
> type Var = Char
> type Label = Int
> type Val = Int

Straight line programs are translated into code for a simple stack-oriented
virtual machine.

A VM code program is a list of commands, where a command is either a load
immdiate (which loads a constant onto the stack), load (which loads the value
of a variable onto the stack), store (which saves the value at the top of the
stack into memory, and deletes it), or an arithmetic operation (which applies
an operations to the two values at the top of the stack and replaces them by
the result).

> type Code = [Command]
>
> data Command = LoadI Val
>              | Load  Var
>              | Store Var
>              | BinOp Op
>              | BoolOp CondOp
>              | Target Label  -- jump target
>              | Jump Label    -- jump to label target
>              | ConJump Label -- jump to label target if Var == 0
>              | StackJump     -- jump to label which is stored on the stack
>              | PushScope     -- push current scope onto scope stack
>              | PopScope      -- override current scope with top of scope stack
>               deriving (Show, Eq)
>
> type Stack = [Int]
> type TStore = [(Var, Type)]
> type VTable = [(ProcName, (Label, TStore))]

Run a program, by compiling and then executing the resulting code
The program is run with an initially empty store and empty stack, and the
output is just the resulting store.  This could be modified to provide an
initial store.

\subsection{run}

> run :: Prog -> Store
> run prog = snd (exec code ([], [], [], code))
>      	     where code = translate prog
>      	           snd (_, s, _, _) = s

\section{Translate}

Translate straight line program into stack machine code

The `translate` function appends a jump to the end of the VM code
at the end of the body code so the program will complete and not run into
the procedure section of the code.
All of the variables and procedures are translated before the main body
so all the critical information is present for checking and translating.

> translate :: Prog -> Code
> translate (Prog procs (Body decl stmts)) = body  ++ [Jump 0] ++ code ++ [Target 0]
>       where tstore = tstore' decl []
>             tstore' [] st = st
>             tstore' ((v, t):vs) st = tstore' vs (setValT v t st)
>             (vtable, code, n) = transprocs procs 1
>             body = trans stmts tstore vtable n

To ensure that every label has a unique label each statement to be translated
will be allocated 2 labels to be unique to it by incrementing the label counter `n`.
Though not all statements need to have a unique label there is no cost expect for incrementing
the `n` variable. Another way of implementing this would be to return the value of `n` form
`trans'` functions and allowing them to update the counter as needed. Though I think that way
makes for much more plumbing code to make sure the next translation gets the new label counter.

> trans :: [Stmt] -> TStore -> VTable -> Label -> Code
> trans [] _ _ _ = []
> trans stmts tstore vtable n = code
>       where checked = check stmts tstore vtable
>             (code, _) = transn checked vtable n
>
> transn :: [Stmt] -> VTable -> Label -> (Code, Int)
> transn [] _ n = ([], n)
> transn (stmt:stmts) vt n = (t ++ rest, m)
>     where (t, m)    = trans' stmt vt  n
>           (rest, _) = transn stmts vt m
>
> trans' :: Stmt -> VTable -> Label -> (Code, Int)
> trans' (Asgn var exp) vt n = ((transexp exp) ++ [Store var], n)
>
> trans' (Call proc exps) vt n = ( [LoadI n] ++ (code exps []) ++ [ PushScope, Jump p, Target n, PopScope], n + 1)
>                   where code [] c = c
>                         code (e:es) c = code es (c ++ transexp e)
>                         p
>                          | not (hasVal proc vt) = error (proc ++ " not found")
>                          | otherwise = fst (getVal proc vt)
>
> trans' (While exp loop) vt n = (cmds, label)
>       where cmds = [Target start] ++ (transexp exp) ++ [ConJump end] ++ loopstmts ++ [Jump start, Target end]
>             (loopstmts, label) = transn loop vt (n + 2)
>             end   = n
>             start = n + 1
>
> trans' (If exp succ fail) vt n = (cmds, label )
>        where cmds =  (transexp exp) ++ [ConJump lfail] ++ truetmt ++ [Jump end, Target lfail] ++ falsestmt  ++ [Target end]
>              (truetmt, n1) = transn succ vt (n + 2)
>              (falsestmt, label) = transn fail vt n1
>              end   = n
>              lfail = n + 1
>
>
> transexp :: Exp -> Code
> transexp (Const n) = [LoadI n]
> transexp (ConstB n) = [LoadI (x n) ]
>           where x True = 1
>                 x False = 0
> transexp (Var v) = [Load v]
> transexp (Bin op e1 e2) = transexp e1 ++ transexp e2 ++ [BinOp op]
> transexp (ConNot exp) = (transexp exp) ++ [BoolOp Not]
> transexp (Con op e1 e2) = transexp e1 ++ transexp e2 ++ [BoolOp op]

A procedure is translated in a similar fashion to how the body of the
program is translated. This includes the static checking of variables.
The main difference is that the entire procedure is wrapped in a `Target`
with the label of the procedure given from the `VTable` and a `StackJump`
to return back to the call site.
The call site is a `Call <procedure name>` statement in the AST which
is translated into storing the return site's label into the stack, jumping and completing
the procedure to be jumped back to the return site's label which is after the jump to
the procedure.

> transprocs :: [Proc] -> Label -> (VTable, Code, Int)
> transprocs [] n = ([], [], n)
> transprocs procs n =  (vtable, code, nn)
>               where (vtable, code, nn) = transprocn procs [] [] n
>
> transprocn :: [Proc] -> VTable -> Code -> Label -> (VTable, Code, Int)
> transprocn [] vt code n = (vt, code, n)
> transprocn (p@(Proc name params (Body decl _)):ps) vt c n = transprocn ps vtable (c ++ code) nn
>           where (code, nn) = transproc p ts vtable n
>                 vtable = setValT name (n, paramts) vt
>                 paramts = tstore' (params) []
>                 ts = tstore' (params ++ decl) []
>                 tstore' [] st = st
>                 tstore' ((v, t):vs) st = tstore' vs (setValT v t st)
>
> transproc :: Proc -> TStore -> VTable -> Label -> (Code, Int)
> transproc (Proc _ _ (Body _ [])) _ _ n = ([], n)
> transproc (Proc _ params (Body _ stmts)) ts vt n = ([Target n] ++ paramexp ++ code ++ [StackJump], nn)
>               where paramexp = paramexp' params
>                     paramexp' [] = []
>                     paramexp' ((n, _):ps) = ((Store n):(paramexp' ps))
>                     checked = check stmts ts vt
>                     (code, nn) =  (transn checked vt (n+1))


\section{check}

This section does static checking on the static constraints of the language.
These include only assigning a variable once, only having one type for a variable
operators being constricted to their types and not being able to declare a variable twice.
`checks` returns a Result ADT which has `Ok` and `Err` so the errors are wrapped and can
be folding & checked by `foldres` so `Err`'s can bubble up. An alternative architecture
would be just throw hard errors. However `check` does throw the `Err` as a hard error.
The `check` function returns the statements that have been validated so it can easily
be used just before the statements are translated in the body or procedures.

> data Result = Ok | Err [Char]
>               deriving (Eq, Show)
>
> check :: [Stmt] -> TStore -> VTable -> [Stmt]
> check [] _ _ = []
> check stmts ts vt = stmts' (checks stmts ts vt)
>           where stmts' (Ok) = stmts
>                 stmts' (Err e) = error e
>
> checks :: [Stmt] -> TStore -> VTable -> Result
> checks [] ss vt = Ok

To check if a procedure call is correct the paramter list is validated and we can assume that since
we got here the procedure is valid.

> checks ((Call name params):ss) s vt = foldres ([has, correctParams] ++ (exps params expts []) ++ [rest])
>       where rest = checks ss s vt
>             expts = snd (getVal name vt)
>             correctParams
>               | length expts == length params = Ok
>               | otherwise = Err "Incorrect number of parameters"
>             exps [] [] c = c
>             exps (e:es) ((_, t):ets) c = exps es ets ((checkexp e t s):c)
>
>             has
>               | hasVal name vt = Ok
>               | otherwise = Err (name ++ " procedure does not exist")
>
> checks ((Asgn v e):ss) store vt = foldres [has, exp, rest]
>       where has
>               | hasVal v store == False = Err ("variable '" ++ [v] ++ "' is not declared")
>               | otherwise = Ok
>             exp = checkexp e (getVal v store) store
>             rest = checks ss store vt
>
> checks ((While e loop):ss) store vt = foldres [exp, t, res]
>       where exp = checkexp e TBool store
>             t = checks loop store vt
>             res =  checks ss store vt
>
> checks ((If e succ fail):ss) store vt = foldres [exp, suc, fai, res]
>           where exp = checkexp e TBool store
>                 suc = checks succ store vt
>                 fai = checks fail store vt
>                 res = checks ss store vt

`foldres` takes a list of results and checks that all of the elements are
`Ok` otherwise returns the 1st `Err` case. 

> foldres :: [Result] -> Result
> foldres [] = Ok
> foldres ((Ok):xs) = foldres xs
> foldres ((Err e):xs) = (Err e)

The `checkexp` function asserts that the types used in the sub-expression
are correct and also checks if the variable has been declared before hand and
if it is the correct type.

> checkexp :: Exp -> Type -> TStore -> Result
> checkexp (Const v) TInt _ = Ok
> checkexp (Const v) _ _ = Err "Const expects Int type"
> checkexp (ConstB v) TBool _ = Ok
> checkexp (ConstB v) _ _ = Err "ConstB expects Bool type"
> checkexp (Var v) t store
>           | not (hasVal v store) = Err "used variable undeclared variable"
>           | getVal v store == t = Ok
>           | otherwise       = Err "used incorrect type for variable"
> checkexp (Bin _ ex1 ex2) TInt s = foldres [(checkexp ex1 TInt s), (checkexp ex2 TInt s)]
> checkexp (ConNot ex1) TBool s    = checkexp ex1 TBool s
> checkexp (Con _ ex1 ex2) TBool s = foldres [(checkexp ex1 TBool s), (checkexp ex2 TBool s)]
> checkexp _ _ _ = Err "incorrect usage of types"

\subsection{VM Code Execute}

Execute a stack code program

> exec :: Code -> (Stack, Store, StoreStack, Code) -> (Stack, Store, StoreStack, Code)
> exec [] ss = ss

The Jump statement enables movement around the Code and is required in
exec because it has control over what is the next command to be executed in
the list. Another option to implement the jump would be to split the jump into
forward and back jumps to allow for the optimised case of foward jumps of just dropping from
the current position and make a backwards jump use the inefficient way of looking from the
start of the VM code. Run time errors are included to ensure correct execution in case
of bugs in translate.

> exec ((Jump t):_) ss@(_, _, _, prog) = exec next ss
>   where nx = dropWhile (\ c -> c /= (Target t)) prog
>         next
>           | nx == [] = error "Label not found"
>           | otherwise = tail nx

Conditional jump is implemented using the same mechanics as the Jump statement.
This was done for ease of use. The Conditional jump relies on there being a value on
the stack to be consumed as the compared value. The Conditional jump (when using Int's as
base) will jump if the value is not equal to 0. Run time errors are included to ensure correct execution in case
of bugs in translate.

> exec ((ConJump t) : _) ([], _, _, _) = error "Conditional jump requires an element on the stack"
> exec ((ConJump t) : cmds) (x:stack, store, sstack, prog) = exec next (stack, store, sstack, prog)
>   where nx = dropWhile (\ c -> c /= (Target t)) prog
>         next
>           | toBool x = cmds
>           | nx == [] = error "Label not found"
>           | otherwise = tail nx
>
> exec ((StackJump) : _) ([], _, _, _) = error "StackJump requires an element in the stack"
> exec ((StackJump) : cmds) (x:stack, store, sstack, prog) = exec next (stack, store, sstack, prog)
>   where nx = dropWhile (\ c -> c /= (Target x)) prog
>         next
>           | nx == [] = error "Label not found"
>           | otherwise = tail nx

PushScope and PopScope are two commands to control the current scope of the store and allow
for having the same name variables in both a procedure and the main body and not mutate each other.
The main motivation for making this a pair of commands is so looking at the VM code it is obvious where
the scopes change. A possible alternative for scopes would be adding a scope label to each value in the
store and increment the scope label when you enter a new scope by would cause difficulties when leaving
a scope as you would have to remove all elements in the store that correlate to the scope, however
it would be easier to implement looking up through scopes to find a variable, such as in an if statement
trying to access a variable defined outside of its inner-scope.

> exec ((PushScope) : cmds) (stack, store, sstack, prog)  = exec cmds (stack, [], store:sstack, prog)
> exec ((PopScope) : cmds) (stack, _, store:sstack, prog) = exec cmds (stack, store, sstack, prog)
>
> exec (cmd : cmds) ss = exec cmds exece
>               where ss' = (a, b, c)
>                     (a, b, s, c) = ss
>                     (x, y, z) = exec' cmd ss'
>                     exece = (x, y, s, z)

> exec' :: Command -> (Stack, Store, Code) -> (Stack, Store, Code)
> exec' (LoadI n) (stack, store, p) = (n:stack, store, p)
> exec' (Load v) (stack, store, p) = (x:stack, store, p)
> 	where x = getVal v store
> exec' (Store v) (x:stack, store, p) = (stack, store', p)
> 	where store' = setVal v x store
> exec' (BinOp op)  (x:y:stack, store, p) = (z:stack, store, p)
> 	where z = apply op x y

Since the stack is a list of Int's to complete boolean operations
the integer values would need to be converted to booleans and back
into integers after completing the calculation. An alternative would
to change the stack to have an ADT representing different types
such as Int's and Bool and hold the real values as they are in Haskell,
however I chose to keep everything as Int's because it makes the VM code layer
to be closer representation of actual compilers.

> exec' (BoolOp Not)  (x:stack, store, p) = (z:stack, store, p)
> 	where z = fromBool (not (toBool x))
> exec' (BoolOp op)  (x:y:stack, store, p) = (z:stack, store, p)
> 	where z = fromBool (apply op (toBool x) (toBool y))
> 	      apply And a b = a && b
> 	      apply Or  a b = a || b

Target is an NOOP here because it is cleaner to handle it in exec' than exec

> exec' (Target _) ss = ss

\subsection{Arithmetic operators}

Apply an arithmetic operator

> apply :: Op -> Int -> Int -> Int
> apply Plus x y = x + y
> apply Minus x y = x - y
> apply Times x y = x * y
> apply Div x y = x `div` y

\section{Stores}

\section{Type checking}

Checks if two values have the same types
and returns the type. This is useful for
asserting the type of an expression.

> cmpType :: Type -> Type -> Type
> cmpType a b
>       | a == b = a
>       | otherwise = error "mismatched types"

Utility functions to handle VM bools into haskell bool's
for ease of use.

> toBool :: Int -> Bool
> toBool n
>       | n == 0 = False
>       | otherwise = True

> fromBool :: Bool -> Int
> fromBool n
>       | False = 0
>       | otherwise = 1

\subsection{Type Store methods}

The `TStore` is used in statically checking the program
for well-formness by using the TStore to track the types of variables
that have been declared and using those declarations to reason the types
of expressions.

\subsection{Set Functions}

Generic sets of pairs for easy of use between `TStore`, `Store` and `VTable`

> hasVal :: (Eq a) => a -> [(a, b)]  -> Bool
> hasVal _ [] = False
> hasVal a ((x, _):xs)
>       | a == x = True
>       | otherwise = hasVal a xs

> getVal :: (Eq a) => a -> [(a, b)] -> b
> getVal _ [] = error "Element does not exist"
> getVal a ((x, y):xs)
>       | a == x = y
>       | otherwise = getVal a xs

> setVal :: (Eq a) => a -> b -> [(a, b)]  -> [(a, b)]
> setVal a b [] = [(a, b)]
> setVal a b (xy@(x, y):xs)
>       | a == x = (x,b):xs
>       | otherwise = xy:(setVal a b xs)


A stronger `setVal` which errors if you override a value

> setValT :: (Eq a) => a -> b -> [(a, b)]  -> [(a, b)]
> setValT a b [] = [(a, b)]
> setValT a b (xy@(x, y):xs)
>       | a == x = error "already exists"
>       | otherwise = xy:(setValT a b xs)

\section{Testing}

\subsection{Programs}

Some examples for testing

> decl = [('a', TInt), ('b', TBool)]
> s1 = Asgn 'b' (ConstB False)
> s2 = Asgn 'a' (Const 0)
> s3 = While (Var 'b') [Asgn 'a' (Bin Plus (Var 'a') (Const 1)), While (Var 'b') [Asgn 'a' (Bin Plus (Var 'a') (Const 1))]]
> p1 = Prog [] (Body decl  [s1, s2, s3])
> p2 = Prog [(Proc "10" [] (Body [('b', TInt)] [(Asgn 'b' (Const 10))]))] (Body [('a', TBool)] [(Asgn 'a' (ConstB False)), (Call "10" [])])
> p3 = Prog [(Proc "x" [('x', TInt)] (Body [] [(Asgn 'x' (Const 10))]))] (Body [('a', TInt)] [(Asgn 'a' (Const 0)), (Call "x" [(Var 'a')])])

> testIfTrue  = run (Prog [] (Body [('a', TInt), ('b', TBool)] [(Asgn 'b' (ConstB True)), If (Var 'b') [(Asgn 'a' (Const 10))] [(Asgn 'a' (Const 0))]])) == [('b', 1), ('a', 10)]
> testIfFalse = run (Prog [] (Body [('a', TInt), ('b', TBool)] [(Asgn 'b' (ConstB False)), If (Var 'b') [(Asgn 'a' (Const 10))] [(Asgn 'a' (Const 0))]])) == [('b', 0), ('a', 0)]
> callsProc   = run (Prog [(Proc "10" [] (Body [('a', TInt)] [(Asgn 'a' (Const 10))]))] (Body [('a', TInt)]  [(Asgn 'a' (Const 0)), (Call "10" [])])) == [('a', 0)]
> callsProc2  = run p2 == [('a', 0)]

Static checks tests which can return `Ok` or `Err e`

> useBeforeDecl = checks [(Asgn 'a' (Const 0))] [] [] == Err "variable 'a' is not declared"
> assgnVar      = checks [(Asgn 'a' (Const 0))] [('a', TInt)] []  == Ok
> misAssgnVar   = checks [(Asgn 'a' (Const 0))] [('a', TBool)] [] == Err "Const expects Int type"
> opWrongType   = checks [(Asgn 'a' (ConNot (ConstB True)))] [('a', TInt)] [] == Err "incorrect usage of types"
> opRightType   = checks [(Asgn 'a' (ConNot (ConstB True)))] [('a', TBool)] [] == Ok
> noProcuder    = checks [(Call "hs" [])] [] [] == Err "hs procedure does not exist"
> hasProcuder   = checks [(Call "hs" [])] [] [("hs", (1, []))] == Ok
> notEnoughParams = checks [(Call "hs" [])] [] [("hs", (1, [('a', TInt)]))] == Err "Incorrect number of parameters"
> wrongTypeParams = checks [(Call "hs" [(Var 'a')])] [('a', TBool)] [("hs", (1, [('a', TInt)]))] == Err "used incorrect type for variable"

> main = do
>
>   putStr("Tests\n")
>   print(testIfTrue)
>   print(testIfFalse)
>   print(callsProc)
>   print(callsProc2)
>
>   print(useBeforeDecl)
>   print(assgnVar)
>   print(misAssgnVar)
>   print(opWrongType)
>   print(opRightType)
>   print(noProcuder)
>   print(hasProcuder)
>   print(notEnoughParams)
>   print(wrongTypeParams)
>
>   putStr("\n\nTest program\n")
>   print(translate p2)
