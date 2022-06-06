from grpc import access_token_call_credentials
import numpy as np
import gym
import random
import pygame
from regex import B
from sympy import primitive


class Agent:
    def __init__(self, i=5, j=0):
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
    def __init__(self, rows=6, columns=6):
        self.env = np.zeros((6,6))
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
            
    
    # TO RECREATE WALLSSS....
    # Para cada caso de can_move_..() adicionar as restrições de paredes
    def is_move_valid(self, act):
        # print(act)
        if act == 0:
            return self.is_valid_new_agent(m.agent.vmove(-1))
        elif act == 1:
            return self.is_valid_new_agent(m.agent.vmove(1)) 
        elif act == 2:
            return self.is_valid_new_agent(m.agent.hmove(-1))
        elif act == 3:
            return self.is_valid_new_agent(m.agent.hmove(1)) 


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
        return 1 if self.has_won() else 0

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
    e[0,5] =  1
    e[4,1] = -1
    e[1,0] = -1
    e[0,3] = -1
    e[4,4] = -1
    e[2,5] = -1
    e[4,3] = -1
    e[2,4] = -1
    e[4,3] = -1
    e[3,2] = -1
    e[2,4] = -1
    e[3,5] = -1
    e[1,0] = -1
    return m


# -----------------------------
# print(f'action size: {action_size}, state size: {state_size}')
action_size = 4 # 4 actions -> UP DOWN LEFT RIGHT
state_size = 36 # 5 x 5 states OR ...
qtable = np.zeros((state_size, action_size))
print(qtable)

# action_size = env.action_space.n
# state_size = env.observation_space.n
# print(f'action size: {action_size}, state size: {state_size}')

# Set hyperparameters for Q-learning

# @hyperparameters

total_episodes = 2000        # Total episodes
max_steps = 100                # Max steps per episode

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
    prev_state = state

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
        
        
#         print(f"exp_exp_tradeoff: {exp_exp_tradeoff}")        
        
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

        # if(new_state == prev_state):
        #     reward = -0.1
        # print(f"new_state: {new_state}, reward: {reward}, done: {done}")

        # Update Q(s,a):= Q(s,a) + lr [R(s,a) + gamma * max Q(s',a') - Q(s,a)]
        # qtable[new_state, :] : all the actions we can take from new state

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
#         print(f'qtable: {qtable}')
        total_rewards = total_rewards + reward
        
#         print(f'total_rewards {total_rewards}')
        prev_state = state
        # Our new state is state
        state = new_state
        action = new_action
#         print(f'new state: {state}')
        
        # If done (if we're dead) : finish episode
        if done == True:
            # print(qtable) 
            break

        # m.visualize()
        # break

    # reduce epsilon (because we need less and less exploration)
    epsilon = min_epsilon + (max_epsilon - min_epsilon)*np.exp(-decay_rate*episode)
    
    rewards.append(total_rewards)
    # m.visualize()

print(qtable)
print ("Score/time: " +  str(sum(rewards)/total_episodes))
print(epsilon)
print(np.argmax(qtable,axis=1).reshape(6,6))

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