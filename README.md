# SickLang - A sick experimental programming language

- Built on top of jvm 

I'm trying to do daily work on this project except Sundays

- functions are first-class citizens
- implicit returns

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

let rss = fn(x, y) {
    x * y
}(4, 5);

let z = fn(k, h) {
    return k(h);
};

z(fn(l) {
    l * 2;
});

let world = "world";
let helloWorld = "hello" + "," + " " + world;

let result = get(5); // "hello"
```