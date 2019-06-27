# HEC EVENT PROCESSOR
The main purpose of this processor is to listen to some important events of the HEC Sakai instance and trigger some actions.

It supports the following configuration options.
```
# Disable the listening of events
# Default: false
# hec.eventprocessing.disable=true

# List of listeners that are listening to events
# Default: * (ALL)
# hec.eventprocessing.listeners=*
# hec.eventprocessing.listeners=GroupEventProcessor,AnotherProcessor
```

## GroupEventProcessor
Everytime a group is updated (site.upd.grp.membership), it enroles the instructors of the section of the first student automatically.

The GroupEventProcessor supports the following configuration:
```
# The instructor roles, the users with this roles in the sections will be added automatically
# Default: {"Instructor","TA", "Teaching Assistant","maintain"}
# hec.eventprocessing.groupeventprocessor.instructor.roles.count=4
# hec.eventprocessing.groupeventprocessor.instructor.roles.1=Instructor
# hec.eventprocessing.groupeventprocessor.instructor.roles.2=TA
# hec.eventprocessing.groupeventprocessor.instructor.roles.3=Teaching Assistant
# hec.eventprocessing.groupeventprocessor.instructor.roles.4=maintain

# The student roles, the users of the first section of the user with this role will be added automatically to the group
# Default: {"Student","access"}
# hec.eventprocessing.groupeventprocessor.student.roles.count=2
# hec.eventprocessing.groupeventprocessor.student.roles.1=Student
# hec.eventprocessing.groupeventprocessor.student.roles.2=access

# Debugging, really useful to debug the entire process
# log.config.count = 1
# log.config.1 = DEBUG.ca.hec.listener.GroupEventProcessor
```

