create table quiz_question (id uuid not null, body varchar(255) not null, created_at timestamp(6) not null, published boolean not null, updated_at timestamp(6) not null, primary key (id))
alter table if exists question_correct_answers add constraint FKdxpovmqlus8cgaxcwt04c65t5 foreign key (question_id) references quiz_question

create table question_correct_answers (question_id uuid not null, correct_answer varchar(255));
alter table if exists question_correct_answers add constraint FK3nr4qylvsx1obopacubbv012h foreign key (question_id) references question;