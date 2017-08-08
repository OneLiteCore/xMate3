# xMate3

If you  are using  [xUtils3](https://github.com/wyouflf/xUtils3) you may know that it is a gengeral lib contains orm, bitmap, http, view. But sometime you only need part of it and using some other libs like ButterKnife to deal with the rest. That is why I make this.

As in my plan, I decide to seperate xUtils3 into 4 divisonal modules. But so far only orm part is available.

# ORM

[ ![Download](https://api.bintray.com/packages/drkcore/maven/xMate3/images/download.svg?version=2.0.2) ](https://bintray.com/drkcore/maven/xMate3/2.0.2/link)

To setup orm part you need to add this in your module build.gradle:

```groovy
compile 'core.mate:xmateDb:2.0.2'
```

Then Init before you use it:

```java
MateDb.init(this);
```

There is two ways to go.

## Extend AbsDb

### Extend AutoDb

## Use DbManager Directly