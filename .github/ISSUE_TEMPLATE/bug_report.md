---
name: Bug report
about: Submit a bug report or plugin crash
title: Plugin bug and/or crash
labels: bug, help wanted
assignees: Dueris

---

name: Bug Report
description: File a bug report
title: "[Bug]: "
labels: ["bug", "triage"]
assignees:
  - octocat
body:
  - type: textarea
    id: sv
    attributes:
      label: What Server Version are you running?
      description: Please tell us the server type, build number, and minecraft version
    validations:
      required: true
  - type: dropdown
    id: version
    attributes:
      label: Version
      description: What version of our software are you running?
      options:
        - 0.2.1
        - 0.1.7
        - 0.1.6
        - 0.1.5
        - 0.1.4
        - 0.1.3
        - Snapshot
        - Github Compile
    validations:
      required: true
  - type: textarea
    id: logs
    attributes:
      label: Provide a Stacktrace or logs
      description: DONT FORGET TO REMOVE IPS!!!!
      render: shell
  - type: textarea
    id: explain
    attributes:
      label: Provide any extra information
      description: it's in the title lol
