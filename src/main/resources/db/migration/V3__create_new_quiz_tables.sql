create table quiz_game_answer (id uuid not null, added_at timestamp(6), answer_status varchar(255) check (answer_status in ('CORRECT','INCORRECT')), quiz_question_id uuid, game_player_progress_id uuid, primary key (id))
create table quiz_game_pair (id uuid not null, finish_game_date timestamp(6), pair_created_date timestamp(6) not null, start_game_date timestamp(6), status varchar(255) check (status in ('PENDING','ACTIVE','FINISHED')), version integer, first_player_id uuid, second_player_id uuid, primary key (id))
create table quiz_game_pair_questions (game_pair_id uuid not null, question_id uuid not null)
create table quiz_game_player_progress (id uuid not null, score integer, version integer, user_id uuid, primary key (id))
create table quiz_question (id uuid not null, body varchar(255) not null, created_at timestamp(6) not null, published boolean not null, updated_at timestamp(6) not null, primary key (id))
create table quiz_question_correct_answers (question_id uuid not null, correct_answers varchar(255))
alter table if exists quiz_game_pair drop constraint if exists UKd6nahgj8a8qrw1otkabonjwmj

alter table if exists quiz_game_pair add constraint UKd6nahgj8a8qrw1otkabonjwmj unique (first_player_id)
alter table if exists quiz_game_pair drop constraint if exists UK9u0tq8dlxlk12rq3orlt12p5n

alter table if exists quiz_game_pair add constraint UK9u0tq8dlxlk12rq3orlt12p5n unique (second_player_id)
alter table if exists quiz_game_answer add constraint FK3mag8od6ruvyyqkqpxj9do6vi foreign key (quiz_question_id) references quiz_question
alter table if exists quiz_game_answer add constraint FKd2y5hihfxmxopd4cb89eebxpt foreign key (game_player_progress_id) references quiz_game_player_progress
alter table if exists quiz_game_pair add constraint FK6g1sy0i9btctahd6tu8c0wu0u foreign key (first_player_id) references quiz_game_player_progress
alter table if exists quiz_game_pair add constraint FKo8s7kuc02fsamp4vy8599ccr7 foreign key (second_player_id) references quiz_game_player_progress
alter table if exists quiz_game_pair_questions add constraint FKmfi2ru3sgnwkwo6yeqmc68qyw foreign key (question_id) references quiz_question
alter table if exists quiz_game_pair_questions add constraint FKtkajuk6fu35r94jbvir2nv35h foreign key (game_pair_id) references quiz_game_pair
alter table if exists quiz_game_player_progress add constraint FKdn1hln23du4k4nv8bo70jum04 foreign key (user_id) references "user"
alter table if exists quiz_question_correct_answers add constraint FKsqbd0ik2pfu7hfkewyvsjiv1o foreign key (question_id) references quiz_question