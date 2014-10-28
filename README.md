sbtflaky
========

Execute test with sbt repeatedly and get a log of failed tests

Usage
=====

```
~$ sbtflaky
     _     _    __ _       _          
 ___| |__ | |_ / _| | __ _| | ___   _ 
/ __| '_ \| __| |_| |/ _` | |/ / | | |
\__ \ |_) | |_|  _| | (_| |   <| |_| |
|___/_.__/ \__|_| |_|\__,_|_|\_\\__, |
                                |___/ 


sbtflaky <max> <test command>

e.g. sbtflaky 10 testOnly *MySpec

~$ 
```

For example:

```
sbtflaky 10 testOnly *JobRepositorySpec
     _     _    __ _       _          
 ___| |__ | |_ / _| | __ _| | ___   _ 
/ __| '_ \| __| |_| |/ _` | |/ / | | |
\__ \ |_) | |_|  _| | (_| |   <| |_| |
|___/_.__/ \__|_| |_|\__,_|_|\_\\__, |
                                |___/ 


Remaining runs: 10 failedRuns: 0 okRuns: 0
Remaining runs: 9 failedRuns: 0 okRuns: 1
Remaining runs: 8 failedRuns: 0 okRuns: 2
```


Installation
============

Just clone the repository, run sbt package and move the bin and 



