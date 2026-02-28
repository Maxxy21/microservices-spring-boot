INSERT INTO question (question_title, option1, option2, option3, option4, right_answer, difficultylevel, category)
VALUES
-- Java questions
('What is the default value of a boolean in Java?', 'true', 'false', 'null', '0', 'false', 'Easy', 'Java'),
('Which keyword is used to inherit a class in Java?', 'implements', 'extends', 'inherits', 'super', 'extends', 'Easy', 'Java'),
('What does JVM stand for?', 'Java Variable Machine', 'Java Virtual Machine', 'Java Verified Module', 'Java Visual Manager', 'Java Virtual Machine', 'Easy', 'Java'),
('Which of these is not a Java primitive type?', 'int', 'boolean', 'String', 'char', 'String', 'Easy', 'Java'),
('What is the size of an int in Java?', '16 bits', '32 bits', '64 bits', '8 bits', '32 bits', 'Easy', 'Java'),
('Which method is the entry point of a Java program?', 'start()', 'run()', 'main()', 'init()', 'main()', 'Easy', 'Java'),
('What is the parent class of all classes in Java?', 'Base', 'Super', 'Object', 'Root', 'Object', 'Medium', 'Java'),
('Which interface must be implemented to create a thread in Java?', 'Runnable', 'Threadable', 'Callable', 'Executable', 'Runnable', 'Medium', 'Java'),
('What does the ''final'' keyword do when applied to a variable?', 'Makes it static', 'Makes it constant', 'Makes it global', 'Makes it volatile', 'Makes it constant', 'Medium', 'Java'),
('Which collection does NOT allow duplicate elements?', 'ArrayList', 'LinkedList', 'HashSet', 'Vector', 'HashSet', 'Medium', 'Java'),
('What is autoboxing in Java?', 'Converting int to long', 'Auto conversion of primitive to wrapper type', 'Memory allocation', 'Garbage collection', 'Auto conversion of primitive to wrapper type', 'Medium', 'Java'),
('Which Java keyword is used to prevent method overriding?', 'static', 'final', 'abstract', 'private', 'final', 'Medium', 'Java'),

-- Python questions
('What is the output of print(type([]))?', '<class ''list''>', '<class ''array''>', '<class ''tuple''>', '<class ''dict''>', '<class ''list''>', 'Easy', 'Python'),
('Which symbol is used for single-line comments in Python?', '//', '#', '--', '/*', '#', 'Easy', 'Python'),
('What keyword is used to define a function in Python?', 'function', 'def', 'fun', 'define', 'def', 'Easy', 'Python'),
('What is the result of 3 ** 2 in Python?', '6', '9', '8', '5', '9', 'Easy', 'Python'),
('Which data type is immutable in Python?', 'list', 'dict', 'set', 'tuple', 'tuple', 'Easy', 'Python'),
('What does ''len()'' function return?', 'Last element', 'Length of object', 'Data type', 'Memory address', 'Length of object', 'Easy', 'Python'),
('What is a lambda function in Python?', 'A named function', 'An anonymous function', 'A recursive function', 'A built-in function', 'An anonymous function', 'Medium', 'Python'),
('Which method adds an element to the end of a list?', 'add()', 'insert()', 'append()', 'extend()', 'append()', 'Easy', 'Python'),
('What does ''self'' refer to in a Python class?', 'The parent class', 'The current instance', 'A static variable', 'The module', 'The current instance', 'Medium', 'Python'),
('Which of these is used for list comprehension?', '()', '{}', '[]', '<>', '[]', 'Medium', 'Python'),

-- SQL questions
('Which SQL statement is used to retrieve data?', 'GET', 'FETCH', 'SELECT', 'RETRIEVE', 'SELECT', 'Easy', 'SQL'),
('What does SQL stand for?', 'Structured Query Language', 'Simple Query Language', 'Standard Query Logic', 'Structured Queue List', 'Structured Query Language', 'Easy', 'SQL'),
('Which clause is used to filter rows in SQL?', 'HAVING', 'WHERE', 'FILTER', 'GROUP BY', 'WHERE', 'Easy', 'SQL'),
('Which SQL keyword removes duplicate rows from a result?', 'UNIQUE', 'DISTINCT', 'DIFFERENT', 'NODUPLICATE', 'DISTINCT', 'Easy', 'SQL'),
('What is a PRIMARY KEY?', 'A key that can be null', 'A unique identifier for a row', 'A foreign reference', 'An index key', 'A unique identifier for a row', 'Easy', 'SQL'),
('Which join returns all rows from both tables?', 'INNER JOIN', 'LEFT JOIN', 'RIGHT JOIN', 'FULL OUTER JOIN', 'FULL OUTER JOIN', 'Medium', 'SQL'),
('Which SQL clause is used with aggregate functions to filter groups?', 'WHERE', 'HAVING', 'GROUP BY', 'ORDER BY', 'HAVING', 'Medium', 'SQL'),
('What does the COUNT() function do?', 'Sums values', 'Counts rows', 'Finds maximum', 'Finds average', 'Counts rows', 'Easy', 'SQL')

ON CONFLICT DO NOTHING;
