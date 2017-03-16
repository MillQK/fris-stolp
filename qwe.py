
with open('/Users/Nikita/Documents/ProjectsIDEA/fris-stolp/datasets/vk_vectors/vk_vectors_full_fix.csv','r') as file:
    with open('/Users/Nikita/Documents/ProjectsIDEA/fris-stolp/datasets/vk_vectors/vk_vectors_full_fix_vect.csv','w') as ovfile:
        with open('/Users/Nikita/Documents/ProjectsIDEA/fris-stolp/datasets/vk_vectors/vk_vectors_full_fix_labels.csv','w') as olfile:
            for line in file:
                splited = line.split(',')
                splited[-1] = splited[-1].replace('\n', '')
                for i in range(3, len(splited)):
                    ovfile.write(splited[i])
                    if (i < len(splited)-1):
                        ovfile.write(',')
                ovfile.write('\n')
                olfile.write(splited[2] + '\n')
