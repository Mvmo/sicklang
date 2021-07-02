# SickLang - A sick experimental programming language

- Built on top of jvm 

I'm trying to do daily work on this project except Sundays

# Builtin functions
- len(string) -> int
```
len("hello, world"); // 12 
```

```
let add = fn(x, y) { x + y };
let subtract = fn(x, y) { return x - y };

let get = fn(x) {
    if (add(x, 5) == 10) {
        "hello"
    } else {
        "world"
    }
}

let result = get(5); // "hello"
```