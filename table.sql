CREATE TABLE public.course
(
    id character varying(10),
    title character varying(30) NOT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE public.course
    OWNER to tdhhbnzaujpwzs;

CREATE TABLE public.takes
    (
        id character varying(150) NOT NULL,
        course_id character varying(10) NOT NULL,
        PRIMARY KEY (id, course_id),
        FOREIGN KEY (id)
            REFERENCES None (username) MATCH SIMPLE
            ON UPDATE CASCADE
            ON DELETE CASCADE
            NOT VALID,
        FOREIGN KEY (course_id)
            REFERENCES None (id) MATCH SIMPLE
            ON UPDATE NO ACTION
            ON DELETE NO ACTION
            NOT VALID
    );

    ALTER TABLE public.takes
        OWNER to tdhhbnzaujpwzs;

CREATE TABLE public.teaches
        (
            inst_id character varying(150) NOT NULL,
            course_id character varying(10) NOT NULL,
            PRIMARY KEY (inst_id, course_id),
            FOREIGN KEY (inst_id)
                REFERENCES None (username) MATCH SIMPLE
                ON UPDATE NO ACTION
                ON DELETE NO ACTION
                NOT VALID,
            FOREIGN KEY (course_id)
                REFERENCES None (id) MATCH SIMPLE
                ON UPDATE NO ACTION
                ON DELETE NO ACTION
                NOT VALID
        );

        ALTER TABLE public.teaches
            OWNER to tdhhbnzaujpwzs;
CREATE TABLE public.class
            (
                id integer NOT NULL,
                inst_id character varying(150) NOT NULL,
                course_id character varying(10) NOT NULL,
                date date NOT NULL,
                "time" time without time zone NOT NULL,
                times integer NOT NULL,
                PRIMARY KEY (id, inst_id, course_id, date, times),
                UNIQUE (id),
                FOREIGN KEY (inst_id)
                    REFERENCES None (username) MATCH SIMPLE
                    ON UPDATE CASCADE
                    ON DELETE CASCADE
                    NOT VALID,
                FOREIGN KEY (course_id)
                    REFERENCES None (id) MATCH SIMPLE
                    ON UPDATE CASCADE
                    ON DELETE CASCADE
                    NOT VALID
            );

ALTER TABLE public.class
                OWNER to tdhhbnzaujpwzs;
CREATE TABLE public.attendance
                (
                    class_id integer NOT NULL,
                    stud_id character varying(150) NOT NULL,
                    PRIMARY KEY (class_id),
                    FOREIGN KEY (class_id)
                        REFERENCES None (id) MATCH SIMPLE
                        ON UPDATE CASCADE
                        ON DELETE CASCADE
                        NOT VALID,
                    FOREIGN KEY (stud_id)
                        REFERENCES None (username) MATCH SIMPLE
                        ON UPDATE CASCADE
                        ON DELETE CASCADE
                        NOT VALID
                );

                ALTER TABLE public.attendance
                    OWNER to tdhhbnzaujpwzs;


CREATE VIEW stat as
SELECT t5.course,
   t5.student,
   t5.inst_id,
   t1.total_classes,
   COALESCE(t5.classes_attended, 0::bigint) AS classes_attended
  FROM (( SELECT takes.course_id AS course,
           takes.stud_id AS student,
           teaches.inst_id
          FROM teaches,
           takes
         WHERE teaches.course_id::text = takes.course_id::text) t3
    LEFT JOIN ( SELECT t0.stud_id,
           t0.course_id2,
           t0.classes_attended
          FROM ( SELECT attendance.stud_id,
                   class.course_id AS course_id2,
                   count(class.course_id) AS classes_attended
                  FROM class,
                   attendance
                 WHERE class.id = attendance.class_id
                 GROUP BY class.course_id, attendance.stud_id) t0) t2 ON t3.student::text = t2.stud_id::text AND t3.course::text = t2.course_id2::text) t5,
   ( SELECT class.course_id AS course_id1,
           count(class.course_id) AS total_classes
          FROM class
         GROUP BY class.course_id) t1
 WHERE t1.course_id1::text = t5.course::text;
