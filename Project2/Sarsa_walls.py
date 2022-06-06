import numpy as np
import gym
import random
import pygame
from sympy import primitive


class Agent:
    def __init__(self, i=4, j=0):
        self.i = i
        self.j = j

    @property
    def loc(self):
        return (self.i, self.j)


    def vmove(self, direction):
        direction = 1 if direction > 0 else -1
        return Agent(self.i + direction, self.j)

    
    def hmove(self, direction):
        direction = 1 if direction > 0 else -1
        return Agent(self.i, self.j + direction)

    def __repr__(self):
        return str(self.loc)

class Maze:
    def __init__(self, rows=5, columns=5):
        self.env = np.zeros((5,5))
        self.agent = Agent()
        self.q_table = np.zeros((rows*columns, 4))

    def state_for_agent(self, a):
        nr, nc = self.env.shape
        return a.i * nc + a.j 

    def in_bounds(self, i, j):
        nr, nc = self.env.shape
        return i >= 0 and i < nr and j >= 0 and j < nc

    def agent_in_bounds(self, a):
        return self.in_bounds(a.i,a.j)

    def agent_would_die(self, a):
        if self.agent_in_bounds(a) : 
            return not self.env[a.i, a.j] == -1
        else :
            return False

    def is_valid_new_agent(self, a):
        return self.agent_in_bounds(a) and self.agent_would_die(a)

    # MAZE 2.
    def can_move_up(self, a):
        if a.i == 1 and a.j == 0:
            return False
        if a.i == 1 and a.j == 1:
            return False
        if a.i == 1 and a.j == 2:
            return False
        if a.i == 1 and a.j == 4:
            return False
        if a.i == 2 and a.j == 1:
            return False
        if a.i == 4 and a.j == 2:
            return False
        if a.i == 4 and a.j == 4:
            return False

        return True

    def can_move_down(self, a):
        if a.i == 0 and a.j == 0:
            return False
        if a.i == 0 and a.j == 1:
            return False
        if a.i == 0 and a.j == 2:
            return False
        if a.i == 0 and a.j == 4:
            return False
        if a.i == 1 and a.j == 1:
            return False
        if a.i == 3 and a.j == 2:
            return False
        if a.i == 3 and a.j == 4:
            return False

        return True
    
    def can_move_left(self, a):
        if a.i == 1 and a.j == 1:
            return False
        if a.i == 2 and a.j == 2:
            return False
        if a.i == 2 and a.j == 4:
            return False

        return True
    
    def can_move_right(self, a):
        if a.i == 1 and a.j == 0:
            return False
        if a.i == 2 and a.j == 1:
            return False
        if a.i == 2 and a.j == 3:
            return False

        # Blocking agent in entrace to check....
        # if a.i == 4 and a.j == 0:
        #     return False
        # if a.i == 3 and a.j == 0:
        #     return False
        # if a.i == 2 and a.j == 0:
        #     return False

        return True
            
    
    # TO RECREATE WALLSSS....
    # Para cada caso de can_move_..() adicionar as restrições de paredes
    def is_move_valid(self, act):
        # print(act)
        if act == 0:
            # MOVE UP (se tiver parede acima) self.can_move_up(m.agent.vmove(-1))
            # print("up ", m.agent.i , " " , m.agent.j)
            # print("total - " , self.is_valid_new_agent(m.agent.vmove(-1)) and self.can_move_up(m.agent))
            return self.is_valid_new_agent(m.agent.vmove(-1)) and self.can_move_up(m.agent)
        elif act == 1:
            # MOVE DOWN (se tiver parede abaixo) self.can_move_down(m.agent.vmove(1))
            # print("down ", m.agent.i , " " , m.agent.j)
            # print("total - " , self.is_valid_new_agent(m.agent.vmove(1)) and self.can_move_down(m.agent))
            return self.is_valid_new_agent(m.agent.vmove(1)) and self.can_move_down(m.agent)
        elif act == 2:
            # MOVE LEFT (se tiver parede a esquerda) self.can_move_left(m.agent.hmove(-1))
            # print("left ", m.agent.i , " " , m.agent.j)
            # print("total - " , self.is_valid_new_agent(m.agent.hmove(-1)) and self.can_move_left(m.agent))
            return self.is_valid_new_agent(m.agent.hmove(-1)) and self.can_move_left(m.agent)
        elif act == 3:
            # MOVE RIGHT (se tiver parede a direita) self.can_move_right(m.agent.hmove(1))
            # print("right ", m.agent.i , " " , m.agent.j)
            # print("total - " , self.is_valid_new_agent(m.agent.hmove(1)) and self.can_move_right(m.agent))
            return self.is_valid_new_agent(m.agent.hmove(1)) and self.can_move_right(m.agent)
        else: print("move valid sucks")


    def compute_possible_moves(self):
        a = self.agent
        moves = [
            a.vmove(1),
            a.vmove(-1),
            a.hmove(1),
            a.hmove(-1),
        ]
        return [m for m in moves if self.is_valid_new_agent(m)]

    def do_a_move(self, a):
        assert self.is_valid_new_agent(a), "Agent cant go there"
        self.agent = a
        return 10 if self.has_won() else 0

    def has_won(self):
        a = self.agent
        return self.env[a.i, a.j] == 1

    def visualize(self):
        assert self.in_bounds(*(self.agent.loc)), "Agent out of bounds"
        e = self.env.copy()
        m = self.agent
        e[m.i, m.j] = 7
        print(e)
        
def make_test_maze():
    m = Maze()
    e = m.env
    e[0,4] =  1
    return m


# -----------------------------
# print(f'action size: {action_size}, state size: {state_size}')
action_size = 4 # 4 actions -> UP DOWN LEFT RIGHT
state_size = 25 # 5 x 5 states OR ...
qtable = np.zeros((state_size, action_size))
print(qtable)

# action_size = env.action_space.n
# state_size = env.observation_space.n
# print(f'action size: {action_size}, state size: {state_size}')

# Set hyperparameters for Q-learning

# @hyperparameters

total_episodes = 2000        # Total episodes
max_steps = 99                # Max steps per episode

learning_rate = 0.8           # Learning rate
gamma = 0.95                  # Discounting rate

# Exploration parameters
epsilon = 1.0                 # Exploration rate
max_epsilon = 1.0             # Exploration probability at start
min_epsilon = 0.01            # Minimum exploration probability 
decay_rate = 0.001             # Exponential decay rate for exploration prob
#I find that decay_rate=0.001 works much better than 0.01

# Learn through Q-learning

# List of rewards
rewards = []

# For life or until learning is stopped
for episode in range(total_episodes):
    # Reset the environment
    m = make_test_maze()
    state = m.state_for_agent(m.agent)
#     print(f"state: {state}")
    step = 0
    done = False
    total_rewards = 0

    while True :
            # Shall we explore or exploit?
            exp_exp_tradeoff = random.uniform(0, 1)

            ## If this number > greater than epsilon --> exploitation 
            #(taking the biggest Q value for this state)
            if exp_exp_tradeoff > epsilon:
                # print(f"qtable[state,:] {qtable[state,:]}")
                action = np.argmax(qtable[state,:])

            # Else doing a random choice --> exploration
            else:
                action = random.randint(0,3)
            
            # print(action)
            # print(m.is_move_valid(action))
            if m.is_move_valid(action):
                break
    
    for step in range(max_steps):
#         print(f"start step...")
        # Choose an action (a) in the current world state (s)
        
        
#         print(f"action is {action}")

        # Take the action (a) and observe the outcome state(s') and reward (r)
        # new_state, reward, done, info = env.step(action)

        # UP DOWN LEFT RIGHT
        if action == 0:
            reward = m.do_a_move(m.agent.vmove(-1))
            new_state = m.state_for_agent(m.agent)
        elif action == 1:
            reward = m.do_a_move(m.agent.vmove(1))
            new_state = m.state_for_agent(m.agent)
        elif action == 2:
            reward = m.do_a_move(m.agent.hmove(-1))
            new_state = m.state_for_agent(m.agent)
        elif action == 3:
            reward = m.do_a_move(m.agent.hmove(1))
            new_state = m.state_for_agent(m.agent)
        # new_state = m.state_for_agent(m.do_a_move(m.agent.))
        done = m.has_won()
#         print(f"new_state: {new_state}, reward: {reward}, done: {done}, info: {info}")

        # Update Q(s,a):= Q(s,a) + lr [R(s,a) + gamma * max Q(s',a') - Q(s,a)]
        # qtable[new_state, :] : all the actions we can take from new state

        # Choose valid move from new_state
        # Choose valid move from new_state
        while True:
            # Shall we explore or exploit?
            exp_exp_tradeoff = random.uniform(0, 1)

            if exp_exp_tradeoff > epsilon:
                new_action = np.argmax(qtable[new_state,:])

                # Else doing a random choice --> exploration
            else:
                new_action = random.randint(0,3)

                # new_action = random.randint(0,3)
                # print("State/Action", new_state, "/", new_action)
            if m.is_move_valid(new_action):
                break

        qtable[state, action] = qtable[state, action] + learning_rate * (reward + gamma * qtable[new_state, new_action] - qtable[state, action])
        # qtable[state, action] = qtable[state, action] + learning_rate * (reward + gamma * np.max(qtable[new_state, :]) - qtable[state, action])
        
#         print(f'qtable: {qtable}')
        
        total_rewards = total_rewards + reward
        
#         print(f'total_rewards {total_rewards}')
        
        # Our new state is state
        state = new_state
        action = new_action
#         print(f'new state: {state}')
        
        # If done (if we're dead) : finish episode
        if done == True: 
            break
        
    # reduce epsilon (because we need less and less exploration)
    epsilon = min_epsilon + (max_epsilon - min_epsilon)*np.exp(-decay_rate*episode)
    
    rewards.append(total_rewards)
    # m.visualize()

print(qtable)
print ("Score/time: " +  str(sum(rewards)/total_episodes))
print(epsilon)
print(np.argmax(qtable,axis=1).reshape(5,5))
# print(np.argmax(qtable,axis=1).reshape(6,6))

for episode in range(1):
    m = make_test_maze()
    state = m.state_for_agent(m.agent)
    step = 0
    done = False
    print("****************************************************")
    print("EPISODE ", episode)

    for step in range(max_steps):
        # Take the action (index) that have the maximum expected future reward given that state
        action = np.argmax(qtable[state,:])
        print(m.state_for_agent(m.agent), " Agent position : ", m.agent.j, " ", m.agent.i, "-act->", action)
        # new_state, reward, done, info = env.step(action)
        if action == 0:
            reward = m.do_a_move(m.agent.vmove(-1))
            new_state = m.state_for_agent(m.agent)
        elif action == 1:
            reward = m.do_a_move(m.agent.vmove(1))
            new_state = m.state_for_agent(m.agent)
        elif action == 2:
            reward = m.do_a_move(m.agent.hmove(-1))
            new_state = m.state_for_agent(m.agent)
        elif action == 3:
            reward = m.do_a_move(m.agent.hmove(1))
            new_state = m.state_for_agent(m.agent)
        
        done = m.has_won()
        if done:
            break
        state = new_state
m = make_test_maze()
m.visualize()