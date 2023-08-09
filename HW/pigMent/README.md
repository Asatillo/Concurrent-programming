Task2 (6p)
BlockingQueue, ReentrantLock, Condition

First, they walk around busy places looking for prey. If they find a pig no bigger than them, they look at it as food. While watching, a hunter may fell prey to another hunter. The hunter hides after findind a suitable target. After hiding the other hungry pigs can no longer see the hunter.

If he did not find prey, or while he was hiding, someone else got to his prey, or the prey got away (e.g. the prey is also a hunter who went hiding or just simply went home), then the hunt is unsuccessful. In other cases, the hunt is successful and the pig will be enriched with half the weight of the prey. The prey is killed in this case. 

Regardless of success, the hunt is exhausting, so the pigs lie down to rest for the day before heading home. They will then become visible to other hunters again. In return, they get the extra mass for photosynthesis. If they are still alive by the time they wake up, then they survived the the current trip. On the way home, the weight loss and division resulting from metabolism take place in a similar way as in the first task.

openArea és aTerribleThingToDO() - 4 pont

During hunting the hunter spots a pig with the help of the queue. After the prey is identified, the hunter tries to hide. This takes pigSleep() amount of time and the hunter can be seen by other predators during this action. To make the pig visible to others it has to register (insert) itself with the openArea object and deregister (remove) itself afterwards. If the openArea.remove(this) command failes, it means the hunter was hunted down by another hunter and is dead now. If the hunter is still alive, but the prey could not be removed from the openArea object, then the hunt was a failure. E.g. the hunt failes if the prey hides (the prey removes itself from the queue) or was killed by another hunter in the meantime.

Implement the hunting mechanism inside the aTerribleThingToDO() method. The eatLight() method should register and deregister similary to the hunt method (pigs are visibles during sunbathing). If the pig cannot remove itself from the queue, then the pig is dead and the method returns false. The run() method now calls both the hunt and sunbathe method in a loop, until one of them returns false.

After a successful hunt the pig writes "Bless me, Father, for I have sinned." to the console.

Improved parallelisation - 2 pont
Use the registration / deregistration mechanism as a form of mutual exclusion. If used properly, multiple synchronizations can be avoided.

Use explicit locks (ReentrantLock)! Use Condition instead of wait-notify

Use the interrupt from pigPool.shutDownNow() to initiate the end of the program. Surviving pigs get overconfident and yell "Look on my works, ye Mighty, and despair!".